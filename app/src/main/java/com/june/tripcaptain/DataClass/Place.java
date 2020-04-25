package com.june.tripcaptain.DataClass;

public class Place {
    private String placeID;
    private String name;
    private Boolean openNow;
    private Double rating;
    private int priceLevel;
    private String photoRef;

    public Place(String placeID, String name, Boolean openNow, Double rating, int priceLevel, String photoRef) {
        this.placeID = placeID;
        this.name = name;
        this.openNow = openNow;
        this.rating = rating;
        this.priceLevel = priceLevel;
        this.photoRef = photoRef;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }
}
