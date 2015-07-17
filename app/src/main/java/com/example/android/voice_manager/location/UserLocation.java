package com.example.android.voice_manager.location;

import android.app.ActionBar;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by Andrew on 6/15/2015.
 */
public class UserLocation {

    public static final String SHARED_PREFERENCE_DISTANCE_ALARM_KEY = "distance_alarm_key";
    public static final int DEFAULT_DISTANCE_ALARM = 5;
    String address = null;
    LatLng user_location;
    LatLng target_location;
    double distance;
    int alarm_distance;

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

    public int getAlarm_distance() {
        return alarm_distance;
    }

    public void setAlarm_distance(int alarm_distance) {
        this.alarm_distance = alarm_distance;
    }

    public static List<Address> addresToGeoLocation(String strAddress, Activity mActivity) {
        List<Address> address = null;
        Geocoder geocoder = new Geocoder(mActivity);

        double[] latitude = {0};
        double[] longtitude = {0};

        try {
            address = geocoder.getFromLocationName(strAddress, 5);
//            if (address == null) {
//                return null;
//            }
            CharSequence[] cs = new String[address.size()];
            for (int i = 0; i < address.size(); i++) {
                cs[i] = address.get(i).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

}
