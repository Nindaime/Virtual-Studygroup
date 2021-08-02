package com.peeplotech.studygroup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextConverter implements RecognitionListener {

    private int AUDIO_RECORD_REQUEST_CODE = 1;
    private Context context;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private EditText editText;

    public SpeechToTextConverter(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;


        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            checkPermission();
        }



        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this.context);
        speechRecognizer.setRecognitionListener(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return speechRecognizer;
    }

    public Intent getSpeechRecognizerIntent(){
        return  speechRecognizerIntent;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_RECORD_REQUEST_CODE);
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle bundle) {

        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        editText.setText(data.get(0));

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }


}
