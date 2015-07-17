package com.example.android.voice_manager;

import android.app.Activity;
import android.app.AlarmManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.util.Log;

import com.example.android.voice_manager.alarm.AlarmManagerHelper;
import com.example.android.voice_manager.location.UserLocation;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andrew on 6/8/2015.
 */
public class AlarmTextProcessing {

    public static int[] createAlarmInSpecificTime(AlarmManagerHelper alarmMgr, String text, TextProcessing.TargetWordSensor targetWordSensor ){
        int hour, min=0;
        int[] ret_val = new int[2];
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(text);
        matcher.find();
        hour = Integer.valueOf(matcher.group(1));
        if(!targetWordSensor.min.equals("")) {
            matcher.find();
            min = Integer.valueOf(matcher.group(1));
        }
        if (!targetWordSensor.afternoon.equals(""))
            hour += 12;

        Calendar calendar = Calendar.getInstance();
        int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour_of_day > hour)
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

//            calendar = Calendar.getInstance();
//            calendar.add(Calendar.SECOND, 10);

        alarmMgr.insertAlarm(calendar);

        ret_val[0] = hour;
        ret_val[1] = min;
        return ret_val;
    }

    public static int[] createAlarmLapseTime(AlarmManagerHelper alarmMgr, String text, TextProcessing.TargetWordSensor targetWordSensor){
        ArrayList<Integer> numbersFromInput = new ArrayList<Integer>();
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(text);
        Calendar calendar = Calendar.getInstance();
        int[] ret_val = new int[2];

        while (matcher.find()) {
            numbersFromInput.add(Integer.valueOf(matcher.group(1)));
        }
        if (numbersFromInput.size() == 2) {
            calendar.add(Calendar.HOUR_OF_DAY, numbersFromInput.get(0));
            calendar.add(Calendar.MINUTE, numbersFromInput.get(1));
        }
        else if (targetWordSensor.hour.equals("") && !targetWordSensor.min.equals("")) {
            calendar.add(Calendar.MINUTE, numbersFromInput.get(0));
        } else if (!targetWordSensor.hour.equals("") && targetWordSensor.min.equals("")) {
            calendar.add(Calendar.HOUR_OF_DAY, numbersFromInput.get(0));
        }
//            calendar = Calendar.getInstance();
//            calendar.add(Calendar.SECOND, 10);
        alarmMgr.insertAlarm(calendar);
        ret_val[0] = calendar.get(Calendar.HOUR_OF_DAY);
        ret_val[1] = calendar.get(Calendar.MINUTE);
        return ret_val;

    }
    public static void locationAlarm(Activity mActivity, String text, Handler mHandler){
        String strAddress = text.replace("抵達", "");
        List<Address> address = null;

        address = UserLocation.addresToGeoLocation(strAddress, mActivity);
        LatLng latLng = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
        UserLocation userLocation = new UserLocation(null, latLng, strAddress);
        mHandler.obtainMessage(MainActivity.MSG_USER_SPEAK_LOCATION, userLocation).sendToTarget();
    }
}
