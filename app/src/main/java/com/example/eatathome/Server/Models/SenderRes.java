package com.example.eatathome.Server.Models;

public class SenderRes {
    public String to;
    public NotificationRes notification;

    public SenderRes(){

    }

    public SenderRes(String to, NotificationRes notification) {
        this.to = to;
        this.notification = notification;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public NotificationRes getNotification() {
        return notification;
    }

    public void setNotification(NotificationRes notification) {
        this.notification = notification;
    }
}
