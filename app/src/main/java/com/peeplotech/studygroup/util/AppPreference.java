package com.peeplotech.studygroup.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppPreference {

    private  Activity activity;
    private static SharedPreferences sharedPref;

    public AppPreference(Activity activity){
        boolean state = activity == null;
        Log.d("screenn", "activity: "+state);
        this.activity = activity;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this.activity);
    }


    public void setUserDylexicScore(String userId,float score){
        boolean isDyslexic = false;

        if(score < 2){
            isDyslexic = true;
        }

        Log.d("screenn","score: "+score);
        Log.d("screenn","isDyslexic: "+isDyslexic);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(userId, isDyslexic);
        editor.apply();
    }


    public static boolean isDyslexic(String userId){

        return sharedPref.getBoolean(userId, false);
    }
}
