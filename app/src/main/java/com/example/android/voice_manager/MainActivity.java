package com.example.android.voice_manager;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.speech.RecognitionListener;

import com.example.android.voice_manager.alarm.*;
import com.example.android.voice_manager.global.GlobalClass;
import com.example.android.voice_manager.location.DistanceOfTwoPoint;
import com.example.android.voice_manager.location.GoogleLocationServiceAPI;
import com.example.android.voice_manager.location.UserLocation;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Fragment {

    public static final int MSG_ALARM = 1;
    public static final int MSG_SPEECH_RESULT = 2;
    public static final int MSG_SETALARM = 3;
    public static final int CODE_MAP = 4;
    public static final int MSG_USER_SPEAK_LOCATION = 5;
    public static final int MSG_DELETE_DESTINATION = 7;
    private final String TAG = "vm:Main";

    //initial UI variables
    private TextView txtSpeechInput;
    private TextView tvOutput;
    private ImageButton btnSpeak;
    private ProgressBar progressBar;
    //initial variables
    private SpeechRecognizer sr;
    private TextProcessing textProcessing;
    private AlarmManagerHelper alarmMgr;
    private UserLocation userLocation = new UserLocation();
    private Handler navigationHandler;
    private boolean mSpeechOn = false;
    GlobalClass globalVariable = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        txtSpeechInput = (TextView) rootView.findViewById(R.id.tv_speechInput);
        tvOutput = (TextView) rootView.findViewById(R.id.tv_result);
        btnSpeak = (ImageButton) rootView.findViewById(R.id.btnSpeak);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        globalVariable = (GlobalClass) getActivity().getApplicationContext();
        progressBar.setVisibility(View.INVISIBLE);
        alarmMgr = new AlarmManagerHelper(getActivity());
        NavigationActivity activity = (NavigationActivity) getActivity();
        userLocation = activity.getUserLocation();
        navigationHandler = activity.getNavigationHandler();

        alarmMgr = new AlarmManagerHelper(getActivity());
        textProcessing = new TextProcessing(getActivity(), alarmMgr);

        sr = SpeechRecognizer.createSpeechRecognizer(getActivity());
        sr.setRecognitionListener(new listener());


        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mSpeechOn) {
                    mSpeechOn = true;
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                    sr.startListening(intent);
                    Log.i("TAG", "start");
                } else {
                    mSpeechOn = false;
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    sr.stopListening();
                }

            }
        });
        // hide the action bar
        //getActionBar().hide();

        if (globalVariable.isAlarmActive()) {
            triggerAlarm(globalVariable.getAlarmMessage(), AlarmDialog.ALARM_DIALOG);
        }
        return rootView;
    }


    private void triggerAlarm(String message, int requestCode) {

        globalVariable.setAlarmActive(false);
        AlarmDialog alarmDialog = new AlarmDialog(getActivity(), mHandler);
        alarmDialog.startAlarm(message, requestCode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_MAP:
                if (resultCode == Activity.RESULT_OK) {
                    double lat = data.getDoubleExtra("lat", 0);
                    double lng = data.getDoubleExtra("lng", 0);
                    Log.d(TAG, "lat: " + lat + " lng: " + lng);
                    userLocation.setTarget_location(new LatLng(lat, lng));
                    navigationHandler.obtainMessage(NavigationActivity.MSG_GPS_START).sendToTarget();
                }
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sr.destroy();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = "";
            switch (msg.what) {
                case MSG_ALARM:
                    result = (String) msg.obj;
                    tvOutput.append(result + "\n");
                    break;
                case MSG_SPEECH_RESULT:
                    result = (String) msg.obj;
                    txtSpeechInput.setText(result);
                    break;
                case MSG_SETALARM:
                    alarmMgr.setNextAlarm(getActivity());
                    globalVariable.setAlarmActive(false);
                    //getActivity().getIntent().removeExtra("broadcast");
                    break;
                case MSG_USER_SPEAK_LOCATION:
                    UserLocation ret_userLocation = (UserLocation) msg.obj;
                    Intent mapActivity = new Intent(getActivity(), MapsActivity.class);
                    mapActivity.putExtra("lat", ret_userLocation.getTarget_location().latitude);
                    mapActivity.putExtra("lng", ret_userLocation.getTarget_location().longitude);
                    startActivityForResult(mapActivity, CODE_MAP);
                    userLocation.setAddress(ret_userLocation.getAddress());
                    userLocation.setTarget_location(ret_userLocation.getTarget_location());

                    break;
                case MSG_DELETE_DESTINATION:
                    userLocation.setAddress(null);
                    break;
            }
        }
    };

    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
            progressBar.setIndeterminate(false);
            progressBar.setMax(10);
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
            progressBar.setProgress((int) rmsdB);

        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            mSpeechOn = false;
            Log.d(TAG, "onEndofSpeech");
        }

        public void onError(int error) {
            Log.d(TAG, "error " + error);
            txtSpeechInput.setText("error " + error);
            progressBar.setIndeterminate(true);

        }

        public void onResults(Bundle results) {
            String str = new String();
            String result;
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            //AlarmClockSetting.setAlarm(getActivity(), data.get(0).toString());
            //txtSpeechInput.setText(data.get(0).toString());
            textProcessing.start(mHandler, data.get(0).toString());

        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }


}

