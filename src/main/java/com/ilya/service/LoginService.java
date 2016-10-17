package com.ilya.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.io.Serializable;
import java.util.Map;


/**
 * Created by ilya on 17.10.2016.
 */

@Named
@SessionScoped
public class LoginService implements Serializable {


    private Client client;

    private  String csrfH;
    private  String csrfT;
    private String JSESSIONID;

    public String getCsrfH() {
        return csrfH;
    }

    public String getCsrfT() {
        return csrfT;
    }

    public String getJSESSIONID() {
        return JSESSIONID;
    }

    public void acceptCsrf(){
        this.client = ClientBuilder.newClient();
        Response response = client.target("http://localhost:8080/universe")
                                    .path("header")
                                    .request().get();
        MultivaluedMap<String,Object> map = response.getHeaders();
        csrfH = String.valueOf(map.get("X-CSRF-HEADER"));
        csrfT = String.valueOf(map.get("X-CSRF-TOKEN"));
        Map<String,NewCookie> mm = response.getCookies();
        JSESSIONID = mm.get("JSESSIONID").getValue();
        response.close();
    }

    public void sendLogin(String userName,String password){
        Form form = new Form().param("username",userName).param("password",password).param(csrfH,csrfT);
        Response response = client.target("http://localhost:8080/universe")
                .path("loggin")
                .request().post(Entity.form(form));
        response.getStatusInfo();
        JSESSIONID =  response.getCookies().get("JSESSIONID").getValue();
        response.close();
    }
}
