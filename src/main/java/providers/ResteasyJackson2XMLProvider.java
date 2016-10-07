package providers;

/**
 * Created by ilya on 06.10.2016.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.jboss.resteasy.util.DelegatingOutputStream;
import org.jboss.resteasy.util.FindAnnotation;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.jaxrs.cfg.AnnotationBundleKey;
import com.fasterxml.jackson.jaxrs.util.ClassKey;
import com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider;
import com.fasterxml.jackson.jaxrs.xml.XMLEndpointConfig;

/**
 * Rest Easy does not supply a XML Provider that works with Jackson.
 * This class tries to mimic org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider
 *
 * This class will be redundant once rest easy starts creating a XML provider in one of its packages.
 *
 */
@Provider
@Consumes({ "application/xml", "text/xml" })
@Produces({ "application/xml", "text/xml" })
public class ResteasyJackson2XMLProvider extends JacksonJaxbXMLProvider {

    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        if (FindAnnotation.findAnnotation(aClass, annotations, NoJackson.class) != null)
            return false;
        return super.isReadable(aClass, type, annotations, mediaType);
    }

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        if (FindAnnotation.findAnnotation(aClass, annotations, NoJackson.class) != null)
            return false;
        return super.isWriteable(aClass, type, annotations, mediaType);
    }

    // Currently we need to override readFrom and writeTo because Jackson 2.2.1 does not cache correctly
    // It does not allow to have a ContextResolver that chooses different mappers per Java type.

    private static class ClassAnnotationKey
    {
        private AnnotationBundleKey annotations;
        private ClassKey classKey;
        private int hash;

        private ClassAnnotationKey(Class<?> clazz, Annotation[] annotations)
        {
            this.annotations = new AnnotationBundleKey(annotations, AnnotationBundleKey.class);
            this.classKey = new ClassKey(clazz);
            hash = this.annotations.hashCode();
            hash = 31 * hash + classKey.hashCode();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassAnnotationKey that = (ClassAnnotationKey) o;

            if (!annotations.equals(that.annotations)) return false;
            if (!classKey.equals(that.classKey)) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            return hash;
        }
    }

    protected final ConcurrentHashMap<ClassAnnotationKey, XMLEndpointConfig> _readers
            = new ConcurrentHashMap<ClassAnnotationKey, XMLEndpointConfig>();

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream)
            throws IOException
    {
        ClassAnnotationKey key = new ClassAnnotationKey(type, annotations);
        XMLEndpointConfig endpoint;
        endpoint = _readers.get(key);
        // not yet resolved (or not cached any more)? Resolve!
        if (endpoint == null) {
            XmlMapper mapper = _locateMapperViaProvider(type, mediaType);
            endpoint = _configForReading(mapper, annotations, type);
            _readers.put(key, endpoint);
        }
        ObjectReader reader = endpoint.getReader();
        JsonParser jp = _createParser(reader, entityStream);
        // If null is returned, considered to be empty stream
        if (jp == null || jp.nextToken() == null) {
            return null;
        }
        // [Issue#1]: allow 'binding' to JsonParser
        if (((Class<?>) type) == JsonParser.class) {
            return jp;
        }
        return reader.withType(genericType).readValue(jp);
    }


    protected final ConcurrentHashMap<ClassAnnotationKey, XMLEndpointConfig> _writers
            = new ConcurrentHashMap<ClassAnnotationKey, XMLEndpointConfig>();

    @Override
    public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String,Object> httpHeaders, OutputStream entityStream)throws IOException
    {
        entityStream = new DelegatingOutputStream(entityStream) {
            @Override
            public void flush() throws IOException {
                // don't flush as this is a performance hit on Undertow.
                // and causes chunked encoding to happen.
            }
        };
        ClassAnnotationKey key = new ClassAnnotationKey(type, annotations);
        XMLEndpointConfig endpoint;
        endpoint = _writers.get(key);

        // not yet resolved (or not cached any more)? Resolve!
        if (endpoint == null) {
            XmlMapper mapper = locateMapper(type, mediaType);
            endpoint = _configForWriting(mapper, annotations, type);

            // and cache for future reuse
            _writers.put(key, endpoint);
        }

        ObjectWriter writer = endpoint.getWriter();
        boolean withIndentOutput = false; // no way to replace _serializationConfig

        // we can't cache this.
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Formatted.class)) {
                    withIndentOutput = true;
                    break;
                }
            }
        }

		/* 27-Feb-2009, tatu: Where can we find desired encoding? Within
		*   HTTP headers?
		*/
        JsonEncoding enc = findEncoding(mediaType, httpHeaders);
        JsonGenerator jg = writer.getFactory().createGenerator(entityStream, enc);

        try {
            // Want indentation?
            if (writer.isEnabled(SerializationFeature.INDENT_OUTPUT) || withIndentOutput) {
                jg.useDefaultPrettyPrinter();
            }
            // 04-Mar-2010, tatu: How about type we were given? (if any)
            JavaType rootType = null;

            if (genericType != null && value != null) {
		      /* 10-Jan-2011, tatu: as per [JACKSON-456], it's not safe to just force root
		      *    type since it prevents polymorphic type serialization. Since we really
		      *    just need this for generics, let's only use generic type if it's truly
		      *    generic.
		      */
                if (genericType.getClass() != Class.class) { // generic types are other impls of 'java.lang.reflect.Type'
		         /* This is still not exactly right; should root type be further
		         * specialized with 'value.getClass()'? Let's see how well this works before
		         * trying to come up with more complete solution.
		         */
                    rootType = writer.getTypeFactory().constructType(genericType);
		         /* 26-Feb-2011, tatu: To help with [JACKSON-518], we better recognize cases where
		         *    type degenerates back into "Object.class" (as is the case with plain TypeVariable,
		         *    for example), and not use that.
		         */
                    if (rootType.getRawClass() == Object.class) {
                        rootType = null;
                    }
                }
            }

            // Most of the configuration now handled through EndpointConfig, ObjectWriter
            // but we may need to force root type:
            if (rootType != null) {
                writer = writer.withType(rootType);
            }
            value = endpoint.modifyBeforeWrite(value);
            writer.writeValue(jg, value);
        } finally {
            jg.close();
        }
    }
}
