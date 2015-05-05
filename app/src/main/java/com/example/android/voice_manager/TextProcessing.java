package com.example.android.voice_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.android.voice_manager.alarm.AlarmManagerHelper;

import java.util.ArrayList;
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

        Calendar calendar = Calendar.getInstance();

        if (targetWordSensor.action.equals("") && targetWordSensor.hour.equals("") && targetWordSensor.min.equals("")) {
            return "invalid command";
        }

        //if both hour and minute are not empty and action is 鬧鐘
        //alarm clock in a specific time
        else if (targetWordSensor.action.equals("鬧鐘") && !targetWordSensor.hour.equals("") && !targetWordSensor.min.equals("") && targetWordSensor.duration.equals("")) {
            Pattern pattern = Pattern.compile(".*?(\\d+).*?(\\d+).*");
            Matcher matcher = pattern.matcher(s);
            matcher.find();
            int hour = Integer.valueOf(matcher.group(1));
            int min = Integer.valueOf(matcher.group(2));
            if (!targetWordSensor.afternoon.equals(""))
                hour += 12;

            int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour_of_day > hour)
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);

            //AlarmClockSetting.setAlarm(mActivity, hour, min, -1);
            alarmMgr.setAlarm(calendar);

            return "successful set up a alarm at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

            //alarm clock in how many minutes later
        } else if (targetWordSensor.action.equals("鬧鐘") && (!targetWordSensor.hour.equals("") || !targetWordSensor.min.equals("")) && !targetWordSensor.duration.equals("")) {
            int hour = 0, min = 0;
            ArrayList<Integer> numbersFromInput = new ArrayList<Integer>();


            Pattern pattern = Pattern.compile("(\\d+)");
            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                numbersFromInput.add(Integer.valueOf(matcher.group(1)));
            }
            if (numbersFromInput.size() == 2) {
//                hour = calendar.get(Calendar.HOUR_OF_DAY);
//                min = calendar.get(Calendar.MINUTE) + 1;
//
//                hour += numbersFromInput.get(0);
//                min += numbersFromInput.get(1);
//                if (min > 59) {
//                    hour += 1;
//                    min %= 60;
//                }
//                if (hour > 23)
//                    hour %= 24;
                calendar.add(Calendar.HOUR_OF_DAY, numbersFromInput.get(0));
                calendar.add(Calendar.MINUTE, numbersFromInput.get(1));

            }
            else if (targetWordSensor.hour.equals("") && !targetWordSensor.min.equals("")) {
//                hour = calendar.get(Calendar.HOUR_OF_DAY);
//                min = calendar.get(Calendar.MINUTE) + 1;
//                min += numbersFromInput.get(0);
//                if (min > 59) {
//                    hour += 1;
//                    min %= 60;
//                }
                calendar.add(Calendar.MINUTE, numbersFromInput.get(0));

            } else if (!targetWordSensor.hour.equals("") && targetWordSensor.min.equals("")) {
//                hour = calendar.get(Calendar.HOUR_OF_DAY);
//                min = calendar.get(Calendar.MINUTE) + 1;
//                hour += numbersFromInput.get(0);
//                if (hour > 23)
//                    hour %= 24;
                calendar.add(Calendar.HOUR_OF_DAY, numbersFromInput.get(1));

            }
            //AlarmClockSetting.setAlarm(mActivity, hour, min, -1);
            //calendar = Calendar.getInstance();
            //calendar.add(Calendar.SECOND, 10);
            alarmMgr.setAlarm(calendar);

            return "successful set up a alarm at " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
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
        //final String speechResult = replaceTextNumberToNumerical(s);
        Message msg = Message.obtain(mHandler, MainActivity.MSG_SPEECH_RESULT, s);
        msg.sendToTarget();
        dialog.setTitle("語音辨識");
        dialog.setMessage(s);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setCancelable(false);
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String returnValue;
                Toast.makeText(mActivity, "確定", Toast.LENGTH_SHORT).show();
                returnValue = process(s);
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
