package com.example.takethraithip.myproject;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    private final String TAG = "Login";
    LoginButton loginButton;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();


           callbackManager = CallbackManager.Factory.create();

           loginButton = (LoginButton) findViewById(R.id.loginBtn);
           loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));





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
                          /* Intent main = new Intent(LoginActivity.this, MainActivity.class);
                           main.putExtra("name", userName.toString());
                           main.putExtra("email", userEmail.toString());
                           main.putExtra("pic", picUrl.toString());
                           startActivity(main);
                           finish();*/
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

        /**************/

        /*****************/
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

                            DocumentReference docRef = firebaseFirestore.collection("users").document(userMail);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()){
                                            Log.d("DocExists","Already have");
                                            Toast.makeText(LoginActivity.this,"Already Have User",Toast.LENGTH_LONG).show();
                                            updateUI();
                                        }else {
                                            Map<String,String> userData = new HashMap<>();
                                            userData.put("name",userName);
                                            userData.put("email",userMail);
                                            userData.put("imgLink",userPic);
                                            usersRef.document(userMail).set(userData);
                                            Toast.makeText(LoginActivity.this,"Registered New User",Toast.LENGTH_LONG).show();

                                            Map<String,String> userStat = new HashMap<>();
                                            userStat.put("light","0");
                                            userStat.put("water","0");
                                            userStat.put("behavior","0");
                                            statRef.document(userMail).set(userStat);

                                            Map<String,String> notiResult = new HashMap<>();
                                            notiResult.put("type","0");
                                            notiResult.put("result","0");
                                            resultRef.document(userMail).set(notiResult);


                                            Map<String,String> plantData = new HashMap<>();
                                            plantData.put("fertilizer","0");
                                            plantData.put("light","0");
                                            plantData.put("water","0");
                                            plantData.put("addWater","0");
                                            plantData.put("addFer","0");
                                            plantData.put("addLight","0");
                                            plantRef.document(userMail).set(plantData);

                                            updateUI();


                                        }
                                    }
                                }
                            });



                            /**push**/
                            //updateUI();
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
