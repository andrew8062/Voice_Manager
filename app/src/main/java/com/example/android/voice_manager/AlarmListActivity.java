package com.example.android.voice_manager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.voice_manager.alarm.AlarmManagerHelper;
import com.example.android.voice_manager.global.GlobalClass;
import com.example.android.voice_manager.location.UserLocation;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Andrew on 5/3/2015.
 */
public class AlarmListActivity extends Fragment implements View.OnClickListener {
    public static final int MSG_CHECK_ALARMS = 1;
    public static final int MSG_DELETE_ALARM = 2;
    private static final String TAG = "vm:alarm_list";

    ListView listView;
    TextView no_alarm_textView;
    TextView destination_textView;
    Button destination_delete_button;
    Button destination_edit_button;
    AlarmBaseAdapter alarmBaseAdapter;
    View rootView;
    AlarmManagerHelper alarmMgr;
    private Handler navigationHandler;
    private Handler handler = new Handler();
    private UserLocation userLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        rootView = inflater.inflate(R.layout.activity_alarm_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.alarm_listview);
        no_alarm_textView = (TextView) rootView.findViewById(R.id.no_alarm_textviwe);
        destination_textView = (TextView) rootView.findViewById(R.id.destination_textview);
        destination_delete_button = (Button) rootView.findViewById(R.id.destination_delete_button);
        destination_edit_button = (Button) rootView.findViewById(R.id.destination_edit_button);

        alarmMgr = new AlarmManagerHelper(getActivity());

        NavigationActivity activity = (NavigationActivity) getActivity();
        userLocation = activity.getUserLocation();
        navigationHandler = activity.getNavigationHandler();
        alarmBaseAdapter = new AlarmBaseAdapter(getActivity(), mHandler);

        listView.setAdapter(alarmBaseAdapter);

        destination_delete_button.setOnClickListener(this);
        destination_edit_button.setOnClickListener(this);

        check_alarms_exist();
        check_destination_exist();

        return rootView;
    }

    private void check_destination_exist() {
        if (userLocation.getAddress() != null) {
            setupDestinationGuide();
            handler.postDelayed(runnable, 2000);
        } else {
            removeDestinationGuide();
        }
    }

    public void check_alarms_exist() {
        if (alarmBaseAdapter.getCount() == 0)
            no_alarm_textView.setVisibility(rootView.VISIBLE);
        else
            no_alarm_textView.setVisibility(rootView.GONE);
        //getFragmentManager().beginTransaction().replace(R.id.container, this).commit();
    }


    private Runnable runnable = new Runnable() {

        public void run() {
            if (userLocation.getAddress() != null) {
                setupDestinationGuide();
                handler.postDelayed(this, 5000);
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECK_ALARMS:
                    check_alarms_exist();
                    break;
                case MSG_DELETE_ALARM:
                    alarmMgr.deleteAlarm(getActivity());
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.destination_delete_button) {
            userLocation.setAddress(null);
            removeDestinationGuide();
        } else if (v.getId() == R.id.destination_edit_button) {
            Intent mapActivity = new Intent(getActivity(), MapsActivity.class);
            mapActivity.putExtra("lat", userLocation.getTarget_location().latitude);
            mapActivity.putExtra("lng", userLocation.getTarget_location().longitude);
            startActivityForResult(mapActivity, MainActivity.CODE_MAP);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MainActivity.CODE_MAP:
                if (resultCode == Activity.RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 0);
                    double lng = data.getDoubleExtra("lng", 0);
                    Log.d(TAG, "lat: " + lat + " lng: " + lng);
                    userLocation.setTarget_location(new LatLng(lat, lng));
                    userLocation.setAddress("用戶選定");
                }
                break;
        }
    }

    private void removeDestinationGuide() {
        navigationHandler.obtainMessage(NavigationActivity.MSG_GPS_STOP).sendToTarget();
        destination_delete_button.setVisibility(View.INVISIBLE);
        destination_edit_button.setVisibility(View.INVISIBLE);
        destination_textView.setText("currently no destination exist");
    }
    private void setupDestinationGuide(){
        destination_delete_button.setVisibility(View.VISIBLE);
        destination_edit_button.setVisibility(View.VISIBLE);
        destination_textView.setText("目的地: " + userLocation.getAddress() + " 距離: " + String.format("%.2f", userLocation.getDistance()) + "km");
    }
}
