package com.example.carwasher.models;

public class WasherModel
{
    private String username;
    private String email;

    public WasherModel() {
    }

    public WasherModel(String username, String email) {
        this.username = username;
        this.email = email;
        //this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
