package com.ilya.managedBeans;

import com.ilya.model.Login;
import com.ilya.service.LoginService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

/**
 * Created by ilya on 17.10.2016.
 */

@ManagedBean
public class ForLogin {

    private Login login;

    @Inject
    private LoginService loginService;

    @PostConstruct
    public void init(){
        login = new Login();
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public String act(){
        loginService.acceptCsrf();
        loginService.sendLogin(getLogin().getUserName(),getLogin().getPassword());
        return "red";
    }



}
