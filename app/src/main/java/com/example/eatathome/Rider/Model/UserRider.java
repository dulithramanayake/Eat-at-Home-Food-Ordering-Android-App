package com.example.eatathome.Rider.Model;

public class UserRider {

    private String name,phone,password;

    private String restaurantId;

    public UserRider() {
    }

    public UserRider(String name, String phone, String password, String restaurantId) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
