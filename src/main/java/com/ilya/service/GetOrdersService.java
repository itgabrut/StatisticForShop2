package com.ilya.service;

import com.ilya.model.TOrder;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GenericType;


import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @PostConstruct
    public void init(){
        ResteasyProviderFactory instance=ResteasyProviderFactory.getInstance();
        RegisterBuiltin.register(instance);
        instance.registerProvider(ResteasyJackson2Provider.class);
    }

    public TOrder getOrder(){
        TOrder TOrder = null;
        try{
            TOrder = client.target("http://localhost:8080/universe")
                    .path("ws/single")
                    .request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                    .get(TOrder.class);
        }
        catch (WebApplicationException ex) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return TOrder;
    }

    public List<TOrder> getOrders(){

        List<TOrder> list = new ArrayList<TOrder>();
        try{

            list = new ClientRequest("http://localhost:8080/universe/ws/list").get(ClientResponse.class).getEntity(new GenericType<List<TOrder>>(){});
//            list = (List<TOrder>)client.target("http://localhost:8080/universe")
//                    .path("ws/list")
//                    .request(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
//                    .get(ClientResponse.class)
//                    .getEntity(new GenericType<List<TOrder>>(){});
        }
        catch (Exception e){
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return list;
    }
}
