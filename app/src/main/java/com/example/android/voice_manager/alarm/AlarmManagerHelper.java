package com.example.android.voice_manager.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.voice_manager.database.ItemDAO;
import com.example.android.voice_manager.heaptree.Heap;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Andrew on 4/27/2015.
 */
public class AlarmManagerHelper  {

    AlarmManager alarmMgr;
    Context mContext;
    ItemDAO itemDAO;
    //Heap mHeap;
    public AlarmManagerHelper(Context context){
        mContext = context;
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
    }
    public void insertAlarm(Calendar calendar){
        //calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 5);
        AlarmItem alarmItem = new AlarmItem(-1, calendar.getTimeInMillis(), true, "alarm");
        itemDAO = new ItemDAO(mContext);
        itemDAO.insert(alarmItem);
        //mHeap.insert(alarmItem);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Toast.makeText(mContext,format.format(Calendar.getInstance().getTime())+"\n"+format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
        if(!checkAlarmExist()){
            setNextAlarm(mContext);
        }
    }
    private boolean checkAlarmExist(){
        boolean returnVaule = (PendingIntent.getBroadcast(mContext, 0, new Intent(  mContext, AlarmAlertBroadcastReciever.class), PendingIntent.FLAG_NO_CREATE) != null);
        return  returnVaule;
    }
    public void setNextAlarm(Context context){
        if (itemDAO == null)
            itemDAO = new ItemDAO(context);
        if (itemDAO.getCount() > 0) {
            AlarmItem alarmItem = itemDAO.getMostCurrent();
            Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmItem.getTime(), pendingIntent);
        }
    }
    public void deleteAlarm(Context context){
        Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingIntent);
        setNextAlarm(context);
    }
}
