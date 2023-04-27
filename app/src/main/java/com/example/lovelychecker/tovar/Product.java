package com.example.lovelychecker.tovar;

import com.google.gson.internal.LinkedTreeMap;

public class Product {
    private String id;
    private String title;
    private byte[] image;
    private Double fromPrice;
    private Double toPrice;

    private Double averageRating;
    private LinkedTreeMap<String, Object> characteristics;

    public Product() {
    }
    public Product(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Double getFromPrice() {
        return fromPrice;
    }

    public void setFromPrice(Double fromPrice) {
        this.fromPrice = fromPrice;
    }

    public Double getToPrice() {
        return toPrice;
    }

    public void setToPrice(Double toPrice) {
        this.toPrice = toPrice;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public LinkedTreeMap<String, Object> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(LinkedTreeMap<String, Object> characteristics) {
        this.characteristics = characteristics;
    }
}
