package com.example.android.voice_manager.textProcessing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;

import com.example.android.voice_manager.MainActivity;
import com.example.android.voice_manager.alarm.AlarmManagerHelper;

/**
 * Created by smes on 2015/4/1.
 */
public class TextProcessing {
    private final String TAG = "vm:textprocessing";
    private String[] targetWord_action = {"鬧鐘", "抵達"};
    private String[] targetWord_hour = {"點", "小時", ""};
    private String[] targetWord_min = {"分", "分鐘"};
    private String[] targetWord_morning = {"上午", "早上"};
    private String[] targetWord_afternoon = {"下午", "晚上"};
    private String[] targetWord_duration = {"後"};
    private Activity mActivity;
    private Handler mHandler;
    private AlarmManagerHelper alarmMgr;

    public TextProcessing(Activity activity, AlarmManagerHelper alarmMgr) {
        this.alarmMgr = alarmMgr;
        mActivity = activity;

    }

    public String process(String s) {

        TargetWordSensor targetWordSensor;
        targetWordSensor = new TargetWordSensor();

        s = replaceTextNumberToNumerical(s);

        targetWordSensor.action = checkTargetWord(targetWord_action, s);
        targetWordSensor.hour = checkTargetWord(targetWord_hour, s);
        targetWordSensor.min = checkTargetWord(targetWord_min, s);
        targetWordSensor.morning = checkTargetWord(targetWord_morning, s);
        targetWordSensor.afternoon = checkTargetWord(targetWord_afternoon, s);
        targetWordSensor.duration = checkTargetWord(targetWord_duration, s);

        Log.d(TAG, "input: " + s);

        if (targetWordSensor.action.equals("") && targetWordSensor.hour.equals("") && targetWordSensor.min.equals("")) {
            return "invalid command";
        }

        //if both hour and minute are not empty and action is 鬧鐘
        //alarm clock in a specific time
        else if (targetWordSensor.action.equals("鬧鐘") && !targetWordSensor.hour.equals("") && targetWordSensor.duration.equals("")) {
            int[] alarm_time;
            alarm_time = AlarmTextProcessing.createAlarmInSpecificTime(alarmMgr, s, targetWordSensor);
            return "successful set up a alarm at " + alarm_time[0] + ":" + String.format("%02d", alarm_time[1]);
        } else if (targetWordSensor.action.equals("鬧鐘") && (!targetWordSensor.hour.equals("") || !targetWordSensor.min.equals("")) && !targetWordSensor.duration.equals("")) {
            int[] alarm_time;
            alarm_time = AlarmTextProcessing.createAlarmLapseTime(alarmMgr, s, targetWordSensor);
            return "successful set up a alarm at " + alarm_time[0] + ":" + String.format("%02d", alarm_time[1]);
        } else if (targetWordSensor.action.equals("抵達")) {
            AlarmTextProcessing.locationAlarm(mActivity, s, mHandler);
            return "success found the geolocaton of the address";
        }
        return "invalid command";
    }

    public String replaceTextNumberToNumerical(String str) {
        str = str.replaceAll("候", "後");
        str = str.replaceAll("十", "10");
        str = str.replaceAll("兩", "2");
        str = str.replaceAll("一", "1");
        str = str.replaceAll("二", "2");
        str = str.replaceAll("三", "3");
        str = str.replaceAll("四", "4");
        str = str.replaceAll("五", "5");
        str = str.replaceAll("六", "6");
        str = str.replaceAll("七", "7");
        str = str.replaceAll("八", "8");
        str = str.replaceAll("九", "9");
        str = str.replaceAll("半小時", "30分");
        str = str.replaceAll("半", "30分");

        return str;
    }

    private String checkTargetWord(String[] targetWord, String s) {
        for (int i = 0; i < targetWord.length; i++) {
            if (s.contains(targetWord[i])) {
                return targetWord[i];
            }
        }
        return "";
    }


    public void start(final Handler mHandler, final String s) {
        this.mHandler = mHandler;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog.setTitle("語音辨識");
        final EditText editText = new EditText(mActivity);
        editText.setText(s);
        dialog.setView(editText);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(false);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String returnValue;
                returnValue = process(editText.getText().toString());
                Message msg = Message.obtain(mHandler, MainActivity.MSG_ALARM, returnValue);
                msg.sendToTarget();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Message msg = Message.obtain(mHandler, MainActivity.MSG_ALARM, "user cancel");
                msg.sendToTarget();
            }
        });
        Message msg = Message.obtain(mHandler, MainActivity.MSG_SPEECH_RESULT, editText.getText().toString());
        msg.sendToTarget();
        dialog.show();
    }

    class TargetWordSensor {
        String action, hour, min, morning, afternoon, duration;

        public TargetWordSensor() {
            action = "";
            hour = "";
            min = "";
            morning = "";
            afternoon = "";
            duration = "";
        }
    }
}
