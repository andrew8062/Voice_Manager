package com.example.android.voice_manager;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Andrew on 5/3/2015.
 */
public class AlarmListActivity extends Fragment {
    public static final int MSG_CHECK_ALARMS = 1;

    ListView listView;
    TextView textView;
    AlarmBaseAdapter alarmBaseAdapter;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        rootView = inflater.inflate(R.layout.activity_alarm_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.alarm_listview);
        textView = (TextView) rootView.findViewById(R.id.alarm_textview);

        alarmBaseAdapter = new AlarmBaseAdapter(getActivity(), mHandler);
        listView.setAdapter(alarmBaseAdapter);

        check_alarms_exist();
        return rootView;
    }

    public void check_alarms_exist() {
        if (alarmBaseAdapter.getCount() == 0)
            textView.setVisibility(rootView.VISIBLE);
        else
            textView.setVisibility(rootView.GONE);
        getFragmentManager().beginTransaction().replace(R.id.container, this).commit();
    }



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CHECK_ALARMS:
                    check_alarms_exist();
                   break;
            }
        }
    };

}
