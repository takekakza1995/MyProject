package com.example.takethraithip.myproject;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    String picUrl,userName,userEmail;
    String photoUri = null,googleName,googleMail;

    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    private final String TAG = "Login";
    LoginButton loginButton;
    FirebaseFirestore firebaseFirestore;
    SignInButton googlebutton;
    TwitterLoginButton twitterLoginButton;
    private static final int RC_SIGN_IN = 1;
    GoogleApiClient mGoogleApiClient;

    ConstraintLayout constraintLayout;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Twitter.initialize(this);

        setContentView(R.layout.activity_login);

        constraintLayout = (ConstraintLayout) findViewById(R.id.login_layer);
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

         mAuth = FirebaseAuth.getInstance();


         callbackManager = CallbackManager.Factory.create();
         loginButton = (LoginButton) findViewById(R.id.loginBtn);
         loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        googlebutton = (SignInButton) findViewById(R.id.google_button);

        twitterLoginButton = findViewById(R.id.twitter_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
                AccountService service = twitterApiClient.getAccountService();
                service.verifyCredentials(false,false,true)
                        .enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        User user = result.data;
                        String twitter_name = user.name;
                        String twitter_mail = user.email;
                        String twitter_pic_url = user.profileImageUrl;
                        //Log.d("TwittData",name + "||||" +mail+ "|||" + pic_url );

                        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name",twitter_name);
                        editor.putString("email",twitter_mail);
                        editor.putString("pic", twitter_pic_url);
                        editor.commit();

                        pushData();

                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                });



                /**************/

                /*************/

                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
            }
        });





/*************Facebook Login****************/
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
               @Override
               public void onSuccess(LoginResult loginResult) {
                   handleFacebookAccessToken(loginResult.getAccessToken());
                   String accessToken = loginResult.getAccessToken().getToken();
                   loginButton.setVisibility(View.INVISIBLE);
                   GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                       @Override
                       public void onCompleted(JSONObject object, GraphResponse response) {

                           Log.d("response", response.toString());

                           getData(object);

                           sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                           SharedPreferences.Editor editor = sharedPreferences.edit();
                           editor.putString("name",userName.toString());
                           editor.putString("email",userEmail.toString());
                           editor.putString("pic",picUrl.toString());
                           editor.commit();

                       }
                   });


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

    /********************/

        googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            })
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build();

    }



    /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();*/



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }
    }

    private void updateUI() {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String testName = sharedPreferences.getString("name","notFound");


        Toast.makeText(LoginActivity.this,"Logged in as" + testName,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

        twitterLoginButton.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                        GoogleSignInAccount account = result.getSignInAccount();

                        account.getEmail();
                        account.getPhotoUrl();
                        account.getDisplayName();

                sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name",account.getDisplayName());
                editor.putString("email",account.getEmail());
                editor.putString("pic", String.valueOf(account.getPhotoUrl()));
                editor.commit();

                pushData();

                        firebaseAuthWithGoogle(account);
                }else {
                //Toast.makeText(LoginActivity.this,"Auth Failed",Toast.LENGTH_SHORT).show();
                }
            }

        /**************/

        /*****************/
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseMessaging messaging = FirebaseMessaging.getInstance();
                            messaging.subscribeToTopic("thraithepProject");

                         /*   if (account.getPhotoUrl() != null){
                                photoUri = account.getPhotoUrl().toString();
                            }
                                googleName = account.getDisplayName().toString();
                                googleMail = account.getEmail().toString();

                            sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("name",googleName.toString());
                            editor.putString("email",googleMail.toString());
                            editor.putString("pic",photoUri.toString());
                            editor.commit();*/

                            pushData();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                           Toast.makeText(LoginActivity.this,"Authen Failed",Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("handle", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("success", "signInWithCredential:success");
                            //check

                            /**push**/

                            FirebaseMessaging messaging = FirebaseMessaging.getInstance();
                            messaging.subscribeToTopic("thraithepProject");

                            pushData();


                            /**push**/

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("failed", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            FirebaseMessaging messaging = FirebaseMessaging.getInstance();
                            messaging.subscribeToTopic("thraithepProject");





                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            /*Toast.makeText(TwitterLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void pushData() {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        final String userName = sharedPreferences.getString("name","notFound");
        final String userMail = sharedPreferences.getString("email","notFound");
        final String userPic = sharedPreferences.getString("pic","notFound");

        firebaseFirestore = FirebaseFirestore.getInstance();

        final CollectionReference usersRef = firebaseFirestore.collection("users");
        final CollectionReference statRef = firebaseFirestore.collection("statistic");
        final CollectionReference resultRef = firebaseFirestore.collection("notiResult");
        final CollectionReference plantRef = firebaseFirestore.collection("plant");
        final CollectionReference weeklyRef = firebaseFirestore.collection("weeklyChart");
        final CollectionReference dailyRef = firebaseFirestore.collection("dailyTask");

        DocumentReference docRef = firebaseFirestore.collection("users").document(userMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        Log.d("DocExists","Already have");
                        //Toast.makeText(LoginActivity.this,"Already Have User",Toast.LENGTH_LONG).show();
                        updateUI();
                    }else {
                        Map<String,String> userData = new HashMap<>();
                        userData.put("name",userName);
                        userData.put("email",userMail);
                        userData.put("imgLink",userPic);
                        usersRef.document(userMail).set(userData);
                        //Toast.makeText(LoginActivity.this,"Registered New User",Toast.LENGTH_LONG).show();

                        Map<String,String> userStat = new HashMap<>();
                        userStat.put("light","0");
                        userStat.put("water","0");
                        userStat.put("behavior","0");
                        statRef.document(userMail).set(userStat);

                        Map<String,String> plantData = new HashMap<>();
                        plantData.put("fertilizer","0");
                        plantData.put("light","0");
                        plantData.put("water","0");
                        plantData.put("addWater","0");
                        plantData.put("addFer","0");
                        plantData.put("addLight","0");
                        plantRef.document(userMail).set(plantData);

                        Map<String,String> dailyTask = new HashMap<>();
                        dailyTask.put("currentDate","null");
                        dailyTask.put("waterTask","0");
                        dailyTask.put("eyeTask","0");
                        dailyTask.put("posTask","0");
                        dailyRef.document(userMail).set(dailyTask);


                        Map<String,Integer> weeklyChart = new HashMap<>();
                        weeklyChart.put("water0",0);
                        weeklyChart.put("water1",0);
                        weeklyChart.put("water2",0);
                        weeklyChart.put("light0",0);
                        weeklyChart.put("light1",0);
                        weeklyChart.put("light2",0);
                        weeklyRef.document(userMail).set(weeklyChart);

                        Map<String,Object> dailyChart = new HashMap<>();
                        dailyChart.put("mon",0);
                        dailyChart.put("tue",0);
                        dailyChart.put("wed",0);
                        dailyChart.put("thu",0);
                        dailyChart.put("fri",0);
                        dailyChart.put("sat",0);
                        dailyChart.put("sun",0);
                        weeklyRef.document(userMail).
                                collection("waterWeek0").document(userMail).set(dailyChart);

                        weeklyRef.document(userMail).
                                collection("waterWeek1").document(userMail).set(dailyChart);

                        weeklyRef.document(userMail).
                                collection("waterWeek2").document(userMail).set(dailyChart);

                        weeklyRef.document(userMail).
                                collection("lightWeek0").document(userMail).set(dailyChart);

                        weeklyRef.document(userMail).
                                collection("lightWeek1").document(userMail).set(dailyChart);

                        weeklyRef.document(userMail).
                                collection("lightWeek2").document(userMail).set(dailyChart);


                        updateUI();



                    }
                }
            }
        });
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
