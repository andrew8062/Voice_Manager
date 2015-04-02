package com.example.android.voice_manager;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by smes on 2015/4/1.
 */
public class AlarmClockSetting {
    private static final String TAG = "vm:alarmSetting";
    private int hour, min;
    public static  void setAlarm(Activity activity, int hour, int min, int dayOfWeek) {
        Log.d(TAG, "alarm setting: "+hour+""+min+""+dayOfWeek);
        Calendar calendar = Calendar.getInstance();
        Intent i = new Intent(android.provider.AlarmClock.ACTION_SET_ALARM);
        i.putExtra(android.provider.AlarmClock.EXTRA_HOUR, hour);
        i.putExtra(android.provider.AlarmClock.EXTRA_MINUTES, min);
        i.putExtra(android.provider.AlarmClock.EXTRA_SKIP_UI, true);
        activity.startActivity(i);
        Toast.makeText(activity, "alarm at "+hour+":"+min, Toast.LENGTH_SHORT).show();

    }
}
