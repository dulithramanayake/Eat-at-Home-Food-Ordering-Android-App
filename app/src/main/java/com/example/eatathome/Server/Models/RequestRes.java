package com.example.eatathome.Server.Models;

import java.util.List;

public class RequestRes {

    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String restaurantId;
    private String paymentMethod;
    private String latLng;
    private List<OrderRes> foods;

    public RequestRes() {

    }

    public RequestRes(String phone, String name, String address, String total, String status, String comment, String restaurantId, String paymentMethod, String latLng, List<OrderRes> foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.restaurantId = restaurantId;
        this.paymentMethod = paymentMethod;
        this.latLng = latLng;
        this.foods = foods;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public List<OrderRes> getFoods() {
        return foods;
    }

    public void setFoods(List<OrderRes> foods) {
        this.foods = foods;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}