package com.example.android.voice_manager.global;

import android.app.Application;

/**
 * Created by Andrew on 6/1/2015.
 */
public class GlobalClass extends Application {
    private boolean isAlarmActive;
    private String alarmMessage;
    private boolean isVibrate;
    private String alarmTonePath;
    public static final String SHARED_PREFERENCE_VIBRATE_SETTING = "vibrate_setting";

    public String getAlarmTonePath() {
        return alarmTonePath;
    }

    public void setAlarmTonePath(String alarmTonePath) {
        this.alarmTonePath = alarmTonePath;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setVibrate(boolean isVibrate) {
        this.isVibrate = isVibrate;
    }

    public String getAlarmMessage() {
        return alarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
        this.alarmMessage = alarmMessage;
    }

    public boolean isAlarmActive() {
        return isAlarmActive;
    }

    public void setAlarmActive(boolean isAlarmActive) {
        this.isAlarmActive = isAlarmActive;
    }

}
