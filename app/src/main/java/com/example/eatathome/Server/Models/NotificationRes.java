package com.example.eatathome.Server.Models;

public class NotificationRes {

    public String body;
    public String title;

    public NotificationRes(){

    }

    public NotificationRes(String body, String title) {
        this.body = body;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
