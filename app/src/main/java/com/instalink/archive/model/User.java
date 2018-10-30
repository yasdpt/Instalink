package com.instalink.archive.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shaon on 8/14/2016.
 */
public class User {

    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
