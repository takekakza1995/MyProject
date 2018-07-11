package com.example.takethraithip.myproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;


public class ScreenStateReciever extends BroadcastReceiver {
    public static final String TAG = "SCREEN_STATE";

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;

    //public static final Integer a = 500000;
    FirebaseMessaging messaging = FirebaseMessaging.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            //code
            Log.d(TAG,"Screen is ON");
         //   messaging.unsubscribeFromTopic("thraithepProject");


        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            //code
         //   messaging.subscribeToTopic("thraithepProject");
           final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   //code


                }
            }, 5000);
            Log.d(TAG,"Screen is OFF");
        }//else
    }
}
