package com.example.android.voice_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.annotation.Target;
import java.sql.Time;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by smes on 2015/4/1.
 */
public class TextProcessing {
    private final String TAG = "vm:textprocessing";
    private String[] targetWord_action = {"鬧鐘"};
    private String[] targetWord_hour = {"點", "小時", ""};
    private String[] targetWord_min = {"分", "分鐘"};
    private String[] targetWord_morning = {"上午", "早上"};
    private String[] targetWord_afternoon = {"下午", "晚上"};
    private String[] targetWord_duration = {"後"};
    private Activity mActivity;
    private Handler mHandler;

    public TextProcessing(Activity activity) {
        mActivity = activity;
    }

    public String process(String s) {
        TargetWordSensor targetWordSensor;
        targetWordSensor = new TargetWordSensor();


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
        else if (targetWordSensor.action.equals("鬧鐘") && !targetWordSensor.hour.equals("") && !targetWordSensor.min.equals("")) {
            Pattern pattern = Pattern.compile(".*?(\\d+).*?(\\d+).*");
            Matcher matcher = pattern.matcher(s);
            matcher.find();
            int hour = Integer.valueOf(matcher.group(1));
            int min = Integer.valueOf(matcher.group(2));
            if (!targetWordSensor.afternoon.equals(""))
                hour += 12;
            AlarmClockSetting.setAlarm(mActivity, hour, min, -1);
            return "successful set up a alarm at " + hour + ":" + min;

            //alarm clock in how many minutes later
        } else if (targetWordSensor.action.equals("鬧鐘") && (!targetWordSensor.hour.equals("") || !targetWordSensor.min.equals("")) && !targetWordSensor.duration.equals("")) {
            Calendar calendar = Calendar.getInstance();
            int hour = 0, min = 0;
//            //一個半小時後
//            if (targetWord_hour.equals("個")) {
//                Pattern pattern = Pattern.compile(".*?(\\d+).*?(\\d+).*");
//                Matcher matcher = pattern.matcher(s);
//                matcher.find();
//                hour = calendar.get(Calendar.HOUR_OF_DAY) + Integer.valueOf(matcher.group(1));
//                min = calendar.get(Calendar.MINUTE) + 1 + Integer.valueOf(matcher.group(2));;
//            }

            //30分鐘後

            Pattern pattern = Pattern.compile(".*?(\\d+).*");
            Matcher matcher = pattern.matcher(s);
            matcher.find();

            hour = calendar.get(Calendar.HOUR_OF_DAY);
            min = calendar.get(Calendar.MINUTE) + 1;
            min += Integer.valueOf(matcher.group(1));
            if (min > 59) {
                hour += 1;
                min %= 60;
            }

            AlarmClockSetting.setAlarm(mActivity, hour, min, -1);
            return "successful set up a alarm at " + hour + ":" + min;
        }
        return "invalid command";
    }

    public String replaceTextNumberToNumerical(String str, boolean returnWithUnit) {
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
        if (returnWithUnit)
            str = str.replaceAll("半", "30分");
        else
            str = str.replaceAll("半", "30");

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


    public void start(final Handler mHandler, String s) {
        this.mHandler = mHandler;
        AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        final String speechResult = replaceTextNumberToNumerical(s, true);
        Message msg = Message.obtain(mHandler, MainActivity.MSG_SPEECH_RESULT, speechResult);
        msg.sendToTarget();
        dialog.setTitle("語音辨識");
        dialog.setMessage(s);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(false);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String returnValue;
                Toast.makeText(mActivity, "確定", Toast.LENGTH_SHORT).show();
                returnValue = process(speechResult);
                Message msg = Message.obtain(mHandler, MainActivity.MSG_ALARM, returnValue);
                msg.sendToTarget();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mActivity, "取消", Toast.LENGTH_SHORT).show();
                Message msg = Message.obtain(mHandler, MainActivity.MSG_ALARM, "user cancel");
                msg.sendToTarget();
            }
        });

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
