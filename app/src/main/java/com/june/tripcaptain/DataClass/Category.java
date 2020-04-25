package com.june.tripcaptain.DataClass;

import java.util.ArrayList;

public class Category {
    private String iconUriStr;
    private String name;
    private ArrayList<String> typeList;

    public Category(String iconUriStr, String name, ArrayList<String> typeList) {
        this.iconUriStr = iconUriStr;
        this.name = name;
        this.typeList = typeList;
    }

    public String getIconUriStr() {
        return iconUriStr;
    }

    public void setIconUriStr(String iconUriStr) {
        this.iconUriStr = iconUriStr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(ArrayList<String> typeList) {
        this.typeList = typeList;
    }
}
