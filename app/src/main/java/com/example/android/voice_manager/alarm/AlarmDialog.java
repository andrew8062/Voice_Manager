package com.example.android.voice_manager.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.view.WindowManager;

import com.example.android.voice_manager.MainActivity;
import com.example.android.voice_manager.NavigationActivity;
import com.example.android.voice_manager.database.ItemDAO;
import com.example.android.voice_manager.global.GlobalClass;


/**
 * Created by Andrew on 4/27/2015.
 */
public class AlarmDialog {
    public static final int ALARM_DIALOG = 0;
    public static final int DESTINATION_DIALOG = 1;

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    //private Context mContext;
    private Activity mActivity;
    private Handler mHandler;
    private GlobalClass globalVariable = null;

    public AlarmDialog(Activity activity, Handler handler) {
        globalVariable = (GlobalClass) activity.getApplicationContext();

        mActivity = activity;
        mHandler = handler;
    }

    public void startAlarm(String message, int requestCode) {

        //remove the current alarm from the database;
        ItemDAO itemDAO = new ItemDAO(mActivity);
        itemDAO.popMostCurrent();

        wakeUpScreen();
        if(globalVariable.isAlarmActive())
            startVibrate();
        startRingTone();
        startDialog(message);

        if (requestCode == ALARM_DIALOG) {
            mHandler.obtainMessage(MainActivity.MSG_SETALARM).sendToTarget();
        }
        else if(requestCode == DESTINATION_DIALOG){
            mHandler.obtainMessage(MainActivity.MSG_DELETE_DESTINATION).sendToTarget();
        }
    }

    private void startDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder
                .setTitle("ALARM!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        if (vibrator != null)
                            vibrator.cancel();

                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        if (vibrator != null)
                            vibrator.cancel();

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startVibrate(){
        vibrator = (Vibrator) mActivity.getSystemService(mActivity.VIBRATOR_SERVICE);
        long[] pattern = {1000, 200, 200, 200};
        vibrator.vibrate(pattern, 0);

    }
    private void startRingTone(){
        String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setVolume(1.0f, 1.0f);
            mediaPlayer.setDataSource(mActivity,
                    Uri.parse(alarmTonePath));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (Exception e) {
            mediaPlayer.release();
        }
    }
    private void wakeUpScreen(){
        //wake up screen
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
