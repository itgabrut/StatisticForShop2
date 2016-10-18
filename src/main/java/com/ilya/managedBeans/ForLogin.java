package com.ilya.managedBeans;

import com.ilya.model.Login;
import com.ilya.service.LoginService;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

/**
 * Created by ilya on 17.10.2016.
 * bean
 */

@ManagedBean(eager = true,name = "loginBean")
@SessionScoped
public class ForLogin {

    private Login login = new Login();

    @Inject
    private LoginService loginService;


    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public String logout(){
        this.login = new Login();
        return "login";
    }

    public String act(){
        loginService.sendLogin(getLogin().getUserName(),getLogin().getPassword());
        return "red";
    }



}
