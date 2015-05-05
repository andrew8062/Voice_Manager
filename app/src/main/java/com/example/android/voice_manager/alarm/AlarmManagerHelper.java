package com.example.android.voice_manager.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.voice_manager.database.ItemDAO;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Andrew on 4/27/2015.
 */
public class AlarmManagerHelper  {

    AlarmManager alarmMgr;
    Context mContext;
    public AlarmManagerHelper(Context context){
        mContext = context;
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
    }
    public void setAlarm(Calendar calendar){
        //calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 5);
        AlarmItem alarmItem = new AlarmItem(-1, calendar.getTimeInMillis(), true, "alarm");
        ItemDAO itemDAO = new ItemDAO(mContext);
        itemDAO.insert(alarmItem);
        Intent myIntent = new Intent(null, Uri.parse(String.valueOf(calendar.getTimeInMillis())), mContext, AlarmAlertBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        //alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent );

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Toast.makeText(mContext,format.format(Calendar.getInstance().getTime())+"\n"+format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();

    }
}
