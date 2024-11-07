package com.example.eatathome.Server.Models;

public class RestaurantRes {

    private String name;
    private String image;
    private String restaurantId;
    private String location;
    private String latitude,longitude;

    public RestaurantRes() {
    }

    public RestaurantRes(String name, String image, String restaurantId, String location, String latitude, String longitude) {
        this.name = name;
        this.image = image;
        this.restaurantId = restaurantId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
