package com.example.carwasher.models;

import java.util.List;

public class RequestModel
{
    String userId;
    String date;
    String requester;
    String location;
    String longitude;
    String latitude;
    String profile;
    String id;
    Boolean status;
    String price;
    List<String> items;

    public RequestModel() {
    }

    public RequestModel(String userId, String date, String requester,
                        String location, String latitude, String longitude
            , String profile, String id, Boolean status, String price,
                        List<String> items) {
        this.userId = userId;
        this.date = date;
        this.requester = requester;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.profile = profile;
        this.id = id;
        this.status = status;
        this.price = price;
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getLocation() {
        return location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
