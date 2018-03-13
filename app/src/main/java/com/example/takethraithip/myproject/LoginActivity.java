package com.example.takethraithip.myproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    String picUrl,userName,userEmail;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //mAuth = FirebaseAuth.getInstance();

           callbackManager = CallbackManager.Factory.create();
           LoginButton loginButton = (LoginButton) findViewById(R.id.loginBtn);
           loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

           loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
               @Override
               public void onSuccess(LoginResult loginResult) {
                   //handleFacebookAccessToken(loginResult.getAccessToken());
                   String accessToken = loginResult.getAccessToken().getToken();

                   GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                       @Override
                       public void onCompleted(JSONObject object, GraphResponse response) {

                           Log.d("response", response.toString());
                           getData(object);

                           Intent main = new Intent(LoginActivity.this, MainActivity.class);
                           main.putExtra("name", userName.toString());
                           main.putExtra("email", userEmail.toString());
                           main.putExtra("pic", picUrl.toString());
                           startActivity(main);
                           finish();
                       }
                   });

                   //เรียก graph
                   Bundle parameters = new Bundle();
                   parameters.putString("fields", "email,name");
                   request.setParameters(parameters);
                   request.executeAsync();
               }

               @Override
               public void onCancel() {

               }

               @Override
               public void onError(FacebookException error) {

               }
           });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }



    private void getData(JSONObject object) {
        try{

            URL profile_picture = new URL ("https://graph.facebook.com/"+object.getString("id")+ "/picture?width=250&height=250");
            //Picasso.with(this).load(profile_picture.toString()).into(userPic);
            //txtName.setText(object.getString("name"));
            //txtMail.setText(object.getString("email"));
            /***/
            picUrl = profile_picture.toString();
            userName = object.getString("name");
            userEmail = object.getString("email");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
