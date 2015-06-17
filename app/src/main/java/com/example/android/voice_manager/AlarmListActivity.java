package com.example.android.voice_manager;

import android.app.AlarmManager;
import android.app.Fragment;
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


/**
 * Created by Andrew on 5/3/2015.
 */
public class AlarmListActivity extends Fragment {
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

        NavigationActivity activity = (NavigationActivity)getActivity();
        userLocation = activity.getUserLocation();

        alarmBaseAdapter = new AlarmBaseAdapter(getActivity(), mHandler);
        listView.setAdapter(alarmBaseAdapter);
        handler.postDelayed(runnable, 1000);
        check_alarms_exist();
        check_destination_exist();
        return rootView;
    }

    private void check_destination_exist() {
        if(userLocation != null){
            destination_delete_button.setVisibility(View.VISIBLE);
            destination_edit_button.setVisibility(View.VISIBLE);
            destination_textView.setText("目的地: "+userLocation.getAddress()+" 距離: "+userLocation.getDistance()+"km");
        }
    }

    public void check_alarms_exist() {
        if (alarmBaseAdapter.getCount() == 0)
            no_alarm_textView.setVisibility(rootView.VISIBLE);
        else
            no_alarm_textView.setVisibility(rootView.GONE);
        getFragmentManager().beginTransaction().replace(R.id.container, this).commit();
    }


    private Runnable runnable = new Runnable()
    {

        public void run()
        {
            Log.d(TAG, "distance: "+userLocation.getDistance());

            handler.postDelayed(this, 5000);
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

}
