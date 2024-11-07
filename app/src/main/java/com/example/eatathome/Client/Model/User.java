package com.example.eatathome.Client.Model;

public class User {

    private String name;
    private String password;
    private String phone;
    private String isstaff;
    private String secureCode;
    private String homeAddress;
    private String image;

    public User(){

    }

    public User(String name, String password, String phone, String secureCode, String homeAddress, String image) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.isstaff = "false";
        this.secureCode = secureCode;
        this.homeAddress = homeAddress;
        this.image = image;
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

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}