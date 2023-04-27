package com.example.lovelychecker;

import com.example.lovelychecker.tovar.Product;

public class Chat {
    private String id;
    private User user;
    private Message lastMessage;
    private Product product;

    public Chat(String id, User user, Message lastMessage, Product product) {
        this.id = id;
        this.user = user;
        this.lastMessage = lastMessage;
        this.product = product;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}

