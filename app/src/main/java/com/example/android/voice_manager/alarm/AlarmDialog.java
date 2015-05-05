package com.example.android.voice_manager.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.WindowManager;

/**
 * Created by Andrew on 4/27/2015.
 */
public class AlarmDialog {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    //private Context mContext;
    private Activity mActivity;

    public AlarmDialog(Activity activity) {
        mActivity = activity;
    }

    public void startAlarm() {

        //wake up screen
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator) mActivity.getSystemService(mActivity.VIBRATOR_SERVICE);
        long[] pattern = {1000, 200, 200, 200};
        vibrator.vibrate(pattern, 0);

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

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder
                .setTitle("ALARM!")
                .setMessage("It's about time!")
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

}
