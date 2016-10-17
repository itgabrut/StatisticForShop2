package com.ilya.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilya.interceptors.annotations.Audited;
import com.ilya.model.TOrder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ilya on 05.10.2016.
 */

@Named
@SessionScoped
public class GetOrdersService implements OrderService , Serializable{


    public GetOrdersService() {
        this.client = ClientBuilder.newClient();
    }

    private Client client;

    @Inject
    private LoginService loginService;

    @Audited
    public List<TOrder> getBetweenDate(String from, String to){
        List<TOrder> list = new ArrayList<TOrder>();
        list = (List<TOrder>) client.target("http://localhost:8080/universe")
                .path("ws/between")
                .queryParam("from",from)
                .queryParam("to",to)
                .request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .get(list.getClass());
        return list;
    }

    public TOrder getOrder(){
        TOrder TOrder = null;
        TOrder = client.target("http://localhost:8080/universe")
                .path("ws/single")
                .request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .get(TOrder.class);
        return TOrder;
    }

    public List<TOrder> getOrders(){
        List<TOrder> list = new ArrayList<TOrder>();
        try{
//            list = new ClientRequest("http://localhost:8080/universe/ws/list").get(ClientResponse.class).getEntity(new GenericType<List<TOrder>>(){});
            list = (List<TOrder>)client.target("http://localhost:8080/universe")
                    .path("ws/list")
                    .request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .get(list.getClass());
//
        }
        catch (Exception e){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return list;
    }

    @Audited
    public int count(Map<String, Object> map)throws IOException {
        return getLazyList(-1,-1,null,null,map).size();
    }

    @Audited
    public List<TOrder> getLazyList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters)throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        List<TOrder> list;
        client.register(ResteasyJackson2Provider.class);
            WebTarget target = client.target("http://localhost:8080/universe")
                    .path("ws/lazy")
                    .queryParam("first",first)
                    .queryParam("pageSize",pageSize)
                    .queryParam("sortField",sortField)
                    .queryParam("sortOrder",sortOrder);
            for(Map.Entry<String,Object> entr : filters.entrySet()){
                target = target.queryParam(entr.getKey(),(String)entr.getValue());
            }
        String  ss = (String) target.request(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("JSESSIONID",loginService.getJSESSIONID()))
                .accept(MediaType.APPLICATION_JSON)
                .get(String.class);

        list = mapper.readValue(ss, new TypeReference<List<TOrder>>(){});
        return list;
    }
}
