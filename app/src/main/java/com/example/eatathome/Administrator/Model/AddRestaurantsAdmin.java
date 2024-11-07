package com.example.eatathome.Administrator.Model;

public class AddRestaurantsAdmin {

    private String name;
    private String password;
    private String phone;
    private String isstaff;
    private String isadmin;
    private String restaurantId;

    public AddRestaurantsAdmin() {
    }

    public AddRestaurantsAdmin(String name, String password, String phone, String restaurantId) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.isstaff = "false";
        this.isadmin = "true";
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsstaff() {
        return isstaff;
    }

    public void setIsstaff(String isstaff) {
        this.isstaff = isstaff;
    }

    public String getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(String isadmin) {
        this.isadmin = isadmin;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
