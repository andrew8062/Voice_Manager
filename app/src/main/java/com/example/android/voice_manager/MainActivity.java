package com.example.android.voice_manager;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.speech.RecognitionListener;

import com.example.android.voice_manager.alarm.*;
import com.example.android.voice_manager.database.ItemDAO;

public class MainActivity extends Fragment {

    public static final int MSG_ALARM = 1;
    public static final int MSG_SPEECH_RESULT = 2;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private TextView txtSpeechInput;
    private TextView tvOutput;
    private ImageButton btnSpeak;
    private ProgressBar progressBar;

    private SpeechRecognizer sr;

    private final String TAG="vm:Main";
    private TextProcessing textProcessing;
    private GoogleLocationServiceAPI googleLocationServiceAPI;
    private AlarmManagerHelper alarmMgr;
    private boolean mSpeechOn = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        boolean startFromBroadcast = getArguments().getBoolean("broadcast");
        txtSpeechInput = (TextView) rootView.findViewById(R.id.tv_speechInput);
        tvOutput = (TextView) rootView.findViewById(R.id.tv_result);
        btnSpeak = (ImageButton) rootView.findViewById(R.id.btnSpeak);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        googleLocationServiceAPI = new GoogleLocationServiceAPI(getActivity());
        alarmMgr = new AlarmManagerHelper(getActivity());

        textProcessing = new TextProcessing(getActivity(), alarmMgr);
        sr = SpeechRecognizer.createSpeechRecognizer(getActivity());
        sr.setRecognitionListener(new listener());


        //googleLocationServiceAPI.start();


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
                }
                else{
                    mSpeechOn = false;
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    sr.stopListening();
                }

            }
        });
        // hide the action bar
        //getActionBar().hide();

        if(startFromBroadcast){
            triggerAlarm();
        }


        return rootView;

    }



    private void triggerAlarm() {
        AlarmDialog alarmDialog = new AlarmDialog(getActivity());
        alarmDialog.startAlarm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sr.destroy();
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            String result = "";
            switch(msg.what){
                case MSG_ALARM:
                    result = (String)msg.obj;
                    tvOutput.append(result+"\n");
                    break;
                case MSG_SPEECH_RESULT:
                    result = (String)msg.obj;
                    txtSpeechInput.setText(result);
                    break;
            }
        }
    };

class listener implements RecognitionListener
{
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "onReadyForSpeech");
    }
    public void onBeginningOfSpeech()
    {
        Log.d(TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);

    }
    public void onRmsChanged(float rmsdB)
    {
        Log.d(TAG, "onRmsChanged");
        progressBar.setProgress((int) rmsdB);

    }
    public void onBufferReceived(byte[] buffer)
    {
        Log.d(TAG, "onBufferReceived");
    }
    public void onEndOfSpeech()
    {
        mSpeechOn = false;
        Log.d(TAG, "onEndofSpeech");
    }
    public void onError(int error)
    {
        Log.d(TAG,  "error " +  error);
        txtSpeechInput.setText("error " + error);
        progressBar.setIndeterminate(true);

    }
    public void onResults(Bundle results)
    {
        String str = new String();
        String result;
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++)
        {
            Log.d(TAG, "result " + data.get(i));
            str += data.get(i);
        }
        //AlarmClockSetting.setAlarm(getActivity(), data.get(0).toString());
        //txtSpeechInput.setText(data.get(0).toString());
        textProcessing.start(mHandler, data.get(0).toString());

    }
    public void onPartialResults(Bundle partialResults)
    {
        Log.d(TAG, "onPartialResults");
    }
    public void onEvent(int eventType, Bundle params)
    {
        Log.d(TAG, "onEvent " + eventType);
    }
}



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


}

