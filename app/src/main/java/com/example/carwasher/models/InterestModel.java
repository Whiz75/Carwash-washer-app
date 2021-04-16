package com.example.carwasher.models;

public class InterestModel {
    private String id;
    private String prices;

    public InterestModel() {
    }

    public InterestModel(String id, String prices) {
        this.id = id;
        this.prices = prices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }
}
