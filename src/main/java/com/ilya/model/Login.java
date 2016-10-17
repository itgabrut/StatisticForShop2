package com.ilya.model;

/**
 * Created by ilya on 17.10.2016.
 */
public class Login {

    private String userName;

    private String password;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {

        return userName;
    }

    public String getPassword() {
        return password;
    }
}
