package com.example.android.voice_manager;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.voice_manager.alarm.AlarmItem;
import com.example.android.voice_manager.database.ItemDAO;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrew on 5/3/2015.
 */
public class AlarmBaseAdapter extends BaseAdapter  {

    ItemDAO itemDAO;
    Activity mActivity;
    List<AlarmItem> alarms;
    private ViewTag viewTag;
    private String TAG = "vm:BaseAdapter";
    private Handler mHandler;
    public AlarmBaseAdapter(Activity activity, Handler handler) {
        mActivity = activity;
        mHandler = handler;
        itemDAO = new ItemDAO(activity);
        alarms = itemDAO.getAll();
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d(TAG, "getView");
        if (null == view) {
            view = LayoutInflater.from(mActivity).inflate(R.layout.alarm_listview_element, null);
        }

        AlarmItem alarm = (AlarmItem) getItem(position);
        Button btn_delete = (Button) view.findViewById(R.id.alarm_listview_btn);
        btn_delete.setOnClickListener(new Button_Click(mActivity, this, position));

        TextView tv_title = (TextView) view.findViewById(R.id.alarm_listvew_title);
        TextView tv_time = (TextView) view.findViewById(R.id.alarm_listvew_time);


        Date date = new Date(alarm.getTime());
        Format format = new SimpleDateFormat("HH:mm:ss");

        tv_title.setText(alarm.getName());
        tv_time.setText(format.format(date));

        return view;
    }




    public class ViewTag {
        TextView text1;
        Button btn1;

        public ViewTag(TextView textview1, Button button1) {
            this.text1 = textview1;
            this.btn1 = button1;
        }
    }


    class Button_Click implements View.OnClickListener {
        private int position;
        private Activity mActivity;
        private AlarmBaseAdapter alarmBaseAdapter;

        public Button_Click(Activity activity, AlarmBaseAdapter alarmBaseAdapter, int position){
            this.position = position;
            this.alarmBaseAdapter = alarmBaseAdapter;
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.alarm_listview_btn){
                AlarmItem alarmItem = alarms.get(position);
                if (alarmItem.getId() == itemDAO.getMostCurrent().getId()){
                    mHandler.obtainMessage(AlarmListActivity.MSG_DELETE_ALARM).sendToTarget();
                }
                itemDAO.delete(alarmItem);
                alarms = itemDAO.getAll();
                alarmBaseAdapter.notifyDataSetChanged();
                mHandler.obtainMessage(AlarmListActivity.MSG_CHECK_ALARMS).sendToTarget();
                Toast.makeText(mActivity, "alarm id: "+alarmItem.getId()+" time: "+alarmItem.getTime(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
