package com.example.android.voice_manager;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by smes on 2015/3/30.
 */
public class SpeechRecognitionListener implements RecognitionListener
{
    private final String TAG="vm:Recognition";
    @Override
    public void onBeginningOfSpeech()
    {
        //Log.d(TAG, "onBeginingOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer)
    {

    }

    @Override
    public void onEndOfSpeech()
    {
        //Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error)
    {
       // mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        //Log.d(TAG, "error = " + error);
    }

    @Override
    public void onEvent(int eventType, Bundle params)
    {

    }

    @Override
    public void onPartialResults(Bundle partialResults)
    {

    }

    @Override
    public void onReadyForSpeech(Bundle params)
    {
        Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
    }

    @Override
    public void onResults(Bundle results)
    {
        //Log.d(TAG, "onResults"); //$NON-NLS-1$
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        // matches are the return values of speech recognition engine
        // Use these values for whatever you wish to do
    }

    @Override
    public void onRmsChanged(float rmsdB)
    {
    }
}