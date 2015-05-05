package com.example.android.voice_manager.alarm;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.example.android.voice_manager.NavigationActivity;

/**
 * Created by Andrew on 4/27/2015.
 */
public class AlarmAlertBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Toast.makeText(context, "I have receive bradcast from alarm manager", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(context, NavigationActivity.class);
        i.putExtra("broadcast", true);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }
}
