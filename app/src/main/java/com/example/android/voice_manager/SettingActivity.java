package com.example.android.voice_manager;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.voice_manager.global.GlobalClass;
import com.example.android.voice_manager.location.UserLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingActivity extends Fragment {
    private SharedPreferences settings;
    private final String TAG = "vm:settingActivity";
    SharedPreferences.Editor editor;
    GlobalClass globalVariable = null;

    private ToggleButton toggle_button_vibrate;
    private Spinner destination_range_spinner;
    private Spinner spinner_ringtone;
    private Spinner spinner_gps_range;
    private UserLocation userLocation;
    private View rootView;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    int prevent_spinner_fire_counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_setting, container, false);

        NavigationActivity activity = (NavigationActivity) getActivity();
        mediaPlayer = new MediaPlayer();
        userLocation = activity.getUserLocation();
        globalVariable = (GlobalClass) getActivity().getApplicationContext();
        settings = getActivity().getSharedPreferences(NavigationActivity.PREFS_NAME, 0);
        editor = settings.edit();

        setup_spinner_alarm_distance();
        setup_toggle_button();
        setup_spinner_ringtone();
        setup_spinner_gps_frequency();

        return rootView;
    }

    private void setup_spinner_gps_frequency() {
        List<String> frequency_value = Arrays.asList(getResources().getStringArray(R.array.destination_range_array));
        int gps_frequency = globalVariable.getGps_frequency();
        spinner_gps_range = (Spinner) rootView.findViewById(R.id.spinner_gps_frequency);
        spinner_gps_range.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, frequency_value));
        spinner_gps_range.setSelection(gps_frequency - 1);
        spinner_gps_range.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                globalVariable.setGps_frequency(Integer.valueOf((String) destination_range_spinner.getSelectedItem()));
                editor.putInt(GlobalClass.SHARED_PREFERENCE_GPS_FREQUENCY, globalVariable.getGps_frequency());
                editor.commit();
                Toast.makeText(getActivity(), "new gps frequency: " + destination_range_spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(getActivity(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void setup_toggle_button() {
        toggle_button_vibrate = (ToggleButton) rootView.findViewById(R.id.toggleButton_vibrate);
        toggle_button_vibrate.setChecked(globalVariable.isVibrate());
        toggle_button_vibrate.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggle_button_vibrate.isChecked()) {

                    globalVariable.setVibrate(false);
                    toggle_button_vibrate.setChecked(globalVariable.isVibrate());
                } else {
                    globalVariable.setVibrate(true);
                    toggle_button_vibrate.setChecked(globalVariable.isVibrate());
                }
                Log.d(TAG, "is vibrated: " + globalVariable.isVibrate());
                Log.d(TAG, "toggle state: " + toggle_button_vibrate.isChecked());
                globalVariable.setVibrate(toggle_button_vibrate.isChecked());
                editor.putBoolean(GlobalClass.SHARED_PREFERENCE_VIBRATE_SETTING, globalVariable.isVibrate());
                editor.commit();

            }
        });
    }

    private void setup_spinner_ringtone() {
        RingtoneManager ringtoneManager = new RingtoneManager(getActivity());
        int spinner_position = 0;
        //search all available ringtones
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = ringtoneManager.getCursor();
        List<String> ringtone_name_list = new ArrayList<>();
        final List<String> ringtone_uri_list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String ringtonePath = ringtoneManager.getRingtoneUri(cursor.getPosition()).toString();
            ringtone_name_list.add(ringtoneManager.getRingtone(cursor.getPosition()).getTitle(getActivity()));
            ringtone_uri_list.add(ringtonePath);
            if (ringtonePath.equals(globalVariable.getAlarmTonePath())) {
                spinner_position = cursor.getPosition();
            }
        }
        cursor.close();
        //setup spinner
        spinner_ringtone = (Spinner) rootView.findViewById(R.id.spinner_ringtone);
        spinner_ringtone.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, ringtone_name_list));
        spinner_ringtone.setSelection(spinner_position);
        spinner_ringtone.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prevent_spinner_fire_counter += 1;
                if (prevent_spinner_fire_counter > 1) {
                    Log.d(TAG, "trying alarm");
                    String alarmTonePath = ringtone_uri_list.get(position);
                    globalVariable.setAlarmTonePath(alarmTonePath);
                    editor.putString(GlobalClass.SHARED_PREFERENCE_RIGHTTONEPATH, alarmTonePath);
                    editor.commit();
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        mediaPlayer.setDataSource(getActivity(),
                                Uri.parse(alarmTonePath));
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (Exception e) {
                        mediaPlayer.release();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setup_spinner_alarm_distance() {
        List<String> destination_range_value = Arrays.asList(getResources().getStringArray(R.array.destination_range_array));
        destination_range_spinner = (Spinner) rootView.findViewById(R.id.destination_spinner);
        destination_range_spinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, destination_range_value));
        destination_range_spinner.setSelection(userLocation.getAlarm_distance() - 1);
        destination_range_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                userLocation.setAlarm_distance(Integer.valueOf((String) destination_range_spinner.getSelectedItem()));
                editor.putInt(UserLocation.SHARED_PREFERENCE_DISTANCE_ALARM_KEY, userLocation.getAlarm_distance());
                editor.commit();
                Toast.makeText(getActivity(), "new distance: " + destination_range_spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(getActivity(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
