package com.example.android.voice_manager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Andrew on 5/3/2015.
 */
public class AlarmListActivity extends Fragment {

    ListView listView;
    AlarmBaseAdapter alarmBaseAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        View rootView = inflater.inflate(R.layout.activity_alarm_list, container, false);
        listView = (ListView) rootView.findViewById(R.id.alarm_listview);

        alarmBaseAdapter = new AlarmBaseAdapter(getActivity());
        listView.setAdapter(alarmBaseAdapter);



        return rootView;
    }
}
