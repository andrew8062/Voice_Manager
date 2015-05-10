package com.example.android.voice_manager.heaptree;

import com.example.android.voice_manager.alarm.AlarmItem;

import java.util.Comparator;

/**
 * Created by Andrew on 5/6/2015.
 */
public class CustomComparator implements Comparator<AlarmItem> {
    @Override
    public int compare(AlarmItem parent, AlarmItem itself) {
        if (parent.getTime() < itself.getTime())
            return 1;
        else
            return 0;
    }


}
