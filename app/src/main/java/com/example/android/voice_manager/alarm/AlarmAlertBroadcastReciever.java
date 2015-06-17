package com.example.android.voice_manager.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.android.voice_manager.global.GlobalClass;
import com.example.android.voice_manager.NavigationActivity;

/**
 * Created by Andrew on 4/27/2015.
 */
public class AlarmAlertBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "I have receive bradcast from alarm manager", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(context, NavigationActivity.class);
        final GlobalClass globalVariable = (GlobalClass) context.getApplicationContext();
        globalVariable.setAlarmActive(true);
        //i.putExtra("broadcast", true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);

    }
}
