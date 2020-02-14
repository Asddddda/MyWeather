package com.example.myweather.locationView;

public class Location {

    private String LocName;

    private String id;

    private String parentLoc;

    public Location(String locName,String parentLoc,String id){
        this.id = id;

        this.LocName = locName;

        this.parentLoc = parentLoc;
    }

    public String getId() {
        return id;
    }

    public String getLocName() {
        return LocName;
    }

    public String getParentLoc() {
        return parentLoc;
    }
}