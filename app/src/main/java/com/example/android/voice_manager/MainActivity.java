package com.example.android.voice_manager;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;
import android.speech.RecognitionListener;


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
    private boolean mSpeechOn = false;
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
        progressBar.setVisibility(View.INVISIBLE);

        textProcessing = new TextProcessing(getActivity());
        sr = SpeechRecognizer.createSpeechRecognizer(getActivity());
        sr.setRecognitionListener(new listener());
        googleLocationServiceAPI = new GoogleLocationServiceAPI(getActivity());
        googleLocationServiceAPI.start();


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

                //promptSpeechInput();
            }
        });
        // hide the action bar
        //getActionBar().hide();

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sr.destroy();
    }


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
//    /**
//     * Showing google speech input dialog
//     */
//    private void promptSpeechInput() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                getString(R.string.speech_prompt));
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(getActivity(),
//                    getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * Receiving speech input
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case REQ_CODE_SPEECH_INPUT: {
//                if (resultCode == getActivity().RESULT_OK && null != data) {
//
//                    ArrayList<String> result = data
//                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    txtSpeechInput.setText(result.get(0));
//                }
//                break;
//            }
//
//        }
//    }

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
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


}

