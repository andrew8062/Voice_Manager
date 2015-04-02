package com.example.android.voice_manager;

import android.app.Activity;
import android.util.Log;

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
    private String[] targetWord_hour = {"點", "小時"};
    private String[] targetWord_min = {"分", "分鐘"};
    private String[] targetWord_morning = {"上午", "早上"};
    private String[] targetWord_afternoon = {"下午", "晚上"};
    private String[] targetWord_duration = {"後"};
    private Activity mActivity;
    public TextProcessing(Activity activity) {
        mActivity = activity;
    }

    public String  start(String s) {
        TargetWordSensor targetWordSensor;
        targetWordSensor = new TargetWordSensor();

        s = replaceTextNumberToNumerical(s);

        targetWordSensor.action = checkTargetWord(targetWord_action, s);
        targetWordSensor.hour = checkTargetWord(targetWord_hour, s);
        targetWordSensor.min = checkTargetWord(targetWord_min, s);
        targetWordSensor.morning = checkTargetWord(targetWord_morning, s);
        targetWordSensor.afternoon = checkTargetWord(targetWord_afternoon, s);
        targetWordSensor.duration = checkTargetWord(targetWord_duration, s);

        Log.d(TAG, "input: "+s);


        if (targetWordSensor.action.equals("") && targetWordSensor.hour.equals("") && targetWordSensor.min.equals("")) {
            return "invalid command";
        }
        //if both hour and minute are not empty and action is 鬧鐘
        else if(targetWordSensor.action.equals("鬧鐘") && !targetWordSensor.hour.equals("") && !targetWordSensor.min.equals("")){
            Pattern pattern = Pattern.compile(".*?(\\d+).*?(\\d+).*");
            Matcher matcher = pattern.matcher(s);
            matcher.find();
            int hour = Integer.valueOf(matcher.group(1));
            int min = Integer.valueOf(matcher.group(2));
            if(!targetWordSensor.afternoon.equals(""))
                hour+=12;
            AlarmClockSetting.setAlarm(mActivity, hour, min, -1);
            return "successful set up a alarm at "+hour+":"+min;
        }
        else if(targetWordSensor.action.equals("鬧鐘") && !targetWordSensor.min.equals("") && !targetWordSensor.duration.equals("")){
            Calendar calendar = Calendar.getInstance();
            Pattern pattern = Pattern.compile(".*?(\\d+).*");
            Matcher matcher = pattern.matcher(s);
            matcher.find();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE) +1;
            min += Integer.valueOf(matcher.group(1));
            if(min > 59){
                hour+=1;
                min%=60;
            }
            AlarmClockSetting.setAlarm(mActivity, hour, min, -1);
            return "successful set up a alarm at "+hour+":"+min;

        }

        return "invalid command";
    }

    private String replaceTextNumberToNumerical(String str) {
        str = str.replaceAll("一", "1");
        str = str.replaceAll("二", "2");
        str = str.replaceAll("三", "3");
        str = str.replaceAll("四", "4");
        str = str.replaceAll("五", "5");
        str = str.replaceAll("六", "6");
        str = str.replaceAll("七", "7");
        str = str.replaceAll("八", "8");
        str = str.replaceAll("九", "9");
        str = str.replaceAll("十", "1");
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
