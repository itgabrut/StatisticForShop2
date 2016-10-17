package com.ilya.interceptors;

import com.ilya.interceptors.annotations.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * Created by ilya on 14.10.2016.
 */
@Audited
@Interceptor
public class AuditedIntr implements Serializable{

    public static Logger logger;

    public AuditedIntr() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @AroundInvoke
    public Object auditMethod(InvocationContext ctx) throws Exception {
        logger.info(String.format("Before method: {%s} params: {%s}",ctx.getMethod(),ctx.getParameters()));
        Object result = ctx.proceed();
        logger.info(String.format("After method ... %s",ctx.getTarget()));
        return result;
    }
}