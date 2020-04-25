package com.june.tripcaptain.DataClass;

import java.util.ArrayList;

public class Trip {
    private ArrayList<Place> placeList;
    private String id;

    public Trip(ArrayList<Place> placeList, String id) {
        this.placeList = placeList;
        this.id = id;
    }

    public ArrayList<Place> getPlaceList() {
        return placeList;
    }

    public void setPlaceList(ArrayList<Place> placeList) {
        this.placeList = placeList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
