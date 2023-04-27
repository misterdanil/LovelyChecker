package com.example.lovelychecker;

import java.util.Date;

public class Message {
    private String id;
    private String text;
    private User user;
    private Date createdOn;

    public Message(String id, String text, User user, Date createdOn) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdOn = createdOn;
    }
    public Message() {
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
