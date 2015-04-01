package com.example.android.voice_manager;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by smes on 2015/4/1.
 */
public class AlarmClockSetting {
    private int hour, min;
    public static  void setAlarm(Activity activity, int hour, int min, int dayOfWeek) {
        Intent i = new Intent(android.provider.AlarmClock.ACTION_SET_ALARM);
        i.putExtra(android.provider.AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(android.provider.AlarmClock.EXTRA_MINUTES, min);
        i.putExtra(android.provider.AlarmClock.EXTRA_SKIP_UI, true);
        activity.startActivity(i);

    }
}
