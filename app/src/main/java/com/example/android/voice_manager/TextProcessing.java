package com.example.android.voice_manager;

import android.util.Log;

import java.lang.annotation.Target;
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

    public TextProcessing() {
    }

    public int[] start(String s) {
        TargetWordSensor targetWordSensor;
        targetWordSensor = new TargetWordSensor();

        s = replaceTextNumberToNumerical(s);

        targetWordSensor.action = checkTargetWord(targetWord_action, s);
        targetWordSensor.hour = checkTargetWord(targetWord_hour, s);
        targetWordSensor.min = checkTargetWord(targetWord_min, s);
        targetWordSensor.morning = checkTargetWord(targetWord_morning, s);
        targetWordSensor.afternoon = checkTargetWord(targetWord_afternoon, s);
        targetWordSensor.duration = checkTargetWord(targetWord_duration, s);

        Pattern pattern = Pattern.compile(".*?(\\d+).*?(\\d+).*");
        Log.d(TAG, "input: "+s);
        Matcher matcher = pattern.matcher(s);


        if (targetWordSensor.action.equals("") || targetWordSensor.hour.equals("") || targetWordSensor.min.equals("")) {
            return new int[]{-1, -1, -1};

        }
        //if both hour and minute are not empty and action is 鬧鐘
        else if(targetWordSensor.action.equals("鬧鐘") && !targetWordSensor.hour.equals("") && !targetWordSensor.min.equals("")){
            matcher.find();
            Log.d(TAG, "group 1: "+matcher.group(1));
            int hour = Integer.valueOf(matcher.group(1));
            int min = Integer.valueOf(matcher.group(2));
            Log.d(TAG, "" + matcher.group());
            return new int[] {1, Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2))};
        }
        return new int[]{-1, -1, -1};

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
