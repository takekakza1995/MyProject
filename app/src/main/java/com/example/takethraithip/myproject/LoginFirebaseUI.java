package com.example.takethraithip.myproject;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginFirebaseUI extends AppCompatActivity implements View.OnClickListener {
    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 0;
    FirebaseAuth auth;

// ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase_ui);
// ...

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
        {

            Log.d("AUTH",auth.getCurrentUser().getEmail());
            Toast.makeText(LoginFirebaseUI.this,"Logged in",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginFirebaseUI.this,MainActivity.class);
            startActivity(intent);
            finish();

        }else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);


        }
        findViewById(R.id.logout).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if (requestCode == RESULT_OK)
            {
                //user loggedin
                Log.d("AUTH",auth.getCurrentUser().getEmail());

            }
            else {
                //user Aunthenticated
                Log.d("AUTH","Failed");
            }

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logout){
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("AUTH", "User logged out");
                            finish();
                        }
                    });
        }
    }

}
