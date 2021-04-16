package com.example.carwasher.models;

public class CompletedReqModel {
    String requester;
    String date;
    String profile;
    String price;

    public CompletedReqModel() {
    }

    public CompletedReqModel(String requester, String date, String profile ,String price) {
        this.requester = requester;
        this.date = date;
        this.profile = profile;
        this.price = price;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
