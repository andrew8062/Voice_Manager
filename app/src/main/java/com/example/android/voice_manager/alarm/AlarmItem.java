package com.example.android.voice_manager.alarm;

/**
 * Created by Andrew on 5/1/2015.
 */
public class AlarmItem {

    private long id;
    private long time;
    private String tonePath;
    private boolean vibrate;
    private String name;


    public AlarmItem(long id, long time, boolean vibrate, String name) {
        this.id = id;
        this.time = time;
        this.vibrate = vibrate;
        this.name = name;
    }
    public AlarmItem(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
