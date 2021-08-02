package com.peeplotech.studygroup;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechConverter {

    TextToSpeech converter;
    public TextToSpeechConverter(Context context){
        converter = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    converter.setLanguage(Locale.UK);
                }
            }
        });
    }

    public void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("text 1 ",text);
            converter.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.d("text 2 ",text);
            converter.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
