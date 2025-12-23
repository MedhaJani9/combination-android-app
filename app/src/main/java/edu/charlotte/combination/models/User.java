package edu.charlotte.combination.models;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private String UserId;

    public User(String name, String email, String password, String UserId){
        this.name =name;
        this.email =email;
        this.password =password;
        this.UserId = UserId;
    }
}
