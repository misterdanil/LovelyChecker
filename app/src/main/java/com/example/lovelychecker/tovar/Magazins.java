package com.example.lovelychecker.tovar;

import java.util.Map;

public class Magazins {
    private String type;

    private String link;
    private Map<String, String> colors;
    private float price;
    private String name;

    public Magazins(String type, String name, float price){
        this.name = name;
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String, String> getColors() {
        return colors;
    }

    public void setColors(Map<String, String> colors) {
        this.colors = colors;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public float getPrice() {
        return price;
    }
}
