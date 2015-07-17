package com.example.android.voice_manager;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
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
    SharedPreferences.Editor editor;
    GlobalClass globalVariable = null;

    private ToggleButton toggle_button_vibrate;
    private Spinner destination_range_spinner;
    private Spinner spinner_ringtone;
    private UserLocation userLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_setting, container, false);

        NavigationActivity activity = (NavigationActivity) getActivity();
        userLocation = activity.getUserLocation();
        globalVariable = (GlobalClass) getActivity().getApplicationContext();
        settings = getActivity().getSharedPreferences(NavigationActivity.PREFS_NAME, 0);
        editor = settings.edit();
        setup_spinner_alarm_distance(rootView);
        setup_toggle_button(rootView);
        setup_spinner_ringtone(rootView);

        return rootView;
    }


    private void setup_toggle_button(View rootView) {
        toggle_button_vibrate = (ToggleButton)rootView.findViewById(R.id.toggleButton_vibrate);
        toggle_button_vibrate.setChecked(globalVariable.isVibrate());
        toggle_button_vibrate.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle_button_vibrate.isChecked()){
                    toggle_button_vibrate.setChecked(!toggle_button_vibrate.isChecked());

                }else{
                    toggle_button_vibrate.setChecked(!toggle_button_vibrate.isChecked());
                }
                globalVariable.setVibrate(toggle_button_vibrate.isChecked());
                editor.putBoolean(GlobalClass.SHARED_PREFERENCE_VIBRATE_SETTING, globalVariable.isVibrate());
                editor.commit();

            }
        });
    }
    private void setup_spinner_ringtone(View rootView) {
        RingtoneManager ringtoneManager = new RingtoneManager(getActivity());
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = ringtoneManager.getCursor();
        List<String> ringtone_list = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            ringtone_list.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
        }
        spinner_ringtone = (Spinner) rootView.findViewById(R.id.spinner_ringtone);
        spinner_ringtone.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.spinner_item, ringtone_list));

    }

    private void setup_spinner_alarm_distance(View rootView){
        List<String> destination_range_value = Arrays.asList(getResources().getStringArray(R.array.destination_range_array));
        destination_range_spinner = (Spinner) rootView.findViewById(R.id.destination_spinner);
        destination_range_spinner.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.spinner_item, destination_range_value));
        destination_range_spinner.setSelection(userLocation.getAlarm_distance()-1);
        destination_range_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                userLocation.setAlarm_distance(Integer.valueOf( (String) destination_range_spinner.getSelectedItem()));
                editor.putInt(UserLocation.SHARED_PREFERENCE_DISTANCE_ALARM_KEY, userLocation.getAlarm_distance());
                editor.commit();
                Toast.makeText(getActivity(), "new distance: "+destination_range_spinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
            }
            public void onNothingSelected(AdapterView arg0) {
                Toast.makeText(getActivity(), "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }
        });
    }

}
