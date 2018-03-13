package com.example.takethraithip.myproject;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by Take Thraithip on 3/1/2018.
 */

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService{

    private static final String TAG = "TAG";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }
}
