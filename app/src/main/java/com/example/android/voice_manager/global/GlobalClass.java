package com.example.android.voice_manager.global;

import android.app.Application;

/**
 * Created by Andrew on 6/1/2015.
 */
public class GlobalClass extends Application {
    private boolean isAlarmActive;
    private int destionaion_alarm_distance;

    public int getDestionaion_alarm_distance() {
        return destionaion_alarm_distance;
    }

    public void setDestionaion_alarm_distance(int destionaion_alarm_distance) {
        this.destionaion_alarm_distance = destionaion_alarm_distance;
    }


    public boolean isAlarmActive() {
        return isAlarmActive;
    }

    public void setAlarmActive(boolean isAlarmActive) {
        this.isAlarmActive = isAlarmActive;
    }

}
