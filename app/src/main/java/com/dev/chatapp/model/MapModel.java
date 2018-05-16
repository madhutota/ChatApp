package com.dev.chatapp.model;

public class MapModel {

    private String latitude;
    private String longitude;
    private String placeName;
    private String address;

    public MapModel() {
    }

    public MapModel(String latitude, String longitude, String placeName, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
