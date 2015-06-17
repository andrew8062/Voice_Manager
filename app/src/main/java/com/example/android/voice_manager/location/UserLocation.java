package com.example.android.voice_manager.location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andrew on 6/15/2015.
 */
public class UserLocation {


    String address;
    LatLng user_location;
    LatLng target_location;
    double distance;

    public UserLocation() {
    }



    public UserLocation(LatLng user_location, LatLng target_location, String address) {
        this.user_location = user_location;
        this.target_location = target_location;
        this.address = address;
    }

    public LatLng getUser_location() {
        return user_location;
    }

    public void setUser_location(LatLng user_location) {
        this.user_location = user_location;
    }

    public LatLng getTarget_location() {
        return target_location;
    }

    public void setTarget_location(LatLng target_location) {
        this.target_location = target_location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
