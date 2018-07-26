package com.example.takethraithip.myproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ConstraintLayout myLayer;
    AnimationDrawable animationDrawable;

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
    TextView navUserName,navUserMail,waterDaily,eyeDaily,posDaily,wDailytxt,eDailytxt,pDailytxt;
    ImageView navProfilePic,notiView,plantView,statView;
    SharedPreferences sharedPreferences;
    Calendar calendar = Calendar.getInstance();

    GoogleSignInClient mGoogleSignInClient;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLayer = (ConstraintLayout) findViewById(R.id.my_layer);
        animationDrawable = (AnimationDrawable) myLayer.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("GetToken", "Refreshed token: " + refreshedToken);

       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this,gso);

        waterDaily = (TextView) findViewById(R.id.waterDaily);
        eyeDaily = (TextView) findViewById(R.id.eyeDaily);
        posDaily = (TextView) findViewById(R.id.posDaily);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

/***Nav***/
        View navHead = navigationView.getHeaderView(0);
        navUserName = (TextView) navHead.findViewById(R.id.userName) ;
        navUserMail = (TextView) navHead.findViewById(R.id.email);
        navProfilePic = (ImageView) navHead.findViewById(R.id.profile_picture);


        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String name1 = sharedPreferences.getString("name","not Found");
        String mail1 = sharedPreferences.getString("email","not found");
        String url1 = sharedPreferences.getString("pic","notfound");

            navUserName.setText(name1);
            navUserMail.setText(mail1);
            Picasso.with(MainActivity.this).load(url1.toString()).into(navProfilePic);

/****Nav***/

/************/

/************/

        /********Click*******/
        notiView = (ImageView) findViewById(R.id.notiView);
        plantView = (ImageView) findViewById(R.id.plantView);
        statView =(ImageView) findViewById(R.id.statView);

        notiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ReminderActivityV2.class);
                startActivity(intent);

            }
        });

        plantView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Plant.class);
                startActivity(intent);

            }
        });

        statView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Statistic.class);
                startActivity(intent);

            }
        });
        /*******Click********/




        getNotificationData();



/****logout***/
/*
            Button logoutButton = (Button) findViewById(R.id.logoutBtn);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    getApplicationContext().getSharedPreferences("userInfo", 0).edit().clear().commit();
                    LoginManager.getInstance().logOut();
                    Intent login = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(login);
                    finish();

                }
            });*/

getDailyTask();

/**logout**/





    }//oncreate

    private void getNotificationData() {

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");


        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try{
                final String getNo = bundle.getString("Noti");
                final int notiKey = Integer.parseInt(getNo);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage("Are you did it?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");// for Doc name
                        final String strDate = mdformat.format(calendar.getTime()); // for Doc name
                        final int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                        final int currentYear = calendar.get(Calendar.YEAR);
                        final int currentMonth = calendar.get(Calendar.MONTH);
                        final int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                        Date dateName = new Date();
                        final String dayOfTheWeek = sdf.format(dateName);


                        final DocumentReference statisticUD = db.collection("statistic").document(mail1);
                        final DocumentReference plantData = db.collection("plant").document(mail1);
                        final CollectionReference userRef = db.collection("users").document(mail1).collection("notiResult");
                        final CollectionReference daily = db.collection("dailyTask");
                        final DocumentReference dailyUpdate = db.collection("dailyTask").document(mail1);


                        if (notiKey == 1){
                            //update water
                            /*******daily********/


                            daily.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            String waterTask = String.valueOf(document.getData().get("waterTask"));
                                            int iwater = Integer.parseInt(waterTask);
                                            dailyUpdate.update("waterTask",String.valueOf(iwater+1));
                                            getDailyTask();

                                        }
                                    }
                                }
                            });

                            /*******daily********/

                            /*********get**********/
                            statisticUD.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("Data789", "DocumentSnapshot data: " + document.getData().get("water"));
                                            String getData = (String) document.getData().get("water");
                                            int dataUD = Integer.parseInt(getData);

                                            statisticUD
                                                    .update("water", String.valueOf(dataUD + 1))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Result", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Result", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            plantData
                                                    .update("water", String.valueOf(dataUD + 5))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("PlantData", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                            /********Update********/

                                            /***********time ************/
                                            userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        Map<String,Object> userData = new HashMap<>();
                                                        userData.put("notiType", String.valueOf(notiKey));
                                                        userData.put("week",currentWeek);
                                                        userData.put("year",currentYear);
                                                        userData.put("month",currentMonth);
                                                        userData.put("dayofyear",currentDay);
                                                        userData.put("dateName",dayOfTheWeek);
                                                        userRef.document(strDate).set(userData);

                                                    }
                                                }
                                            });
                                            /***********time ************/
                                        } else {
                                            Log.d("Data789", "No such document");
                                        }
                                    } else {
                                        Log.d("Data789", "get failed with ", task.getException());
                                    }
                                }
                            });
                            /*********get*********/
                            //end of => if(notiKey == 1)
                        }else if (notiKey == 2){

                            /*******daily********/


                            daily.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            String waterTask = String.valueOf(document.getData().get("eyeTask"));
                                            int ilight = Integer.parseInt(waterTask);
                                            dailyUpdate.update("eyeTask",String.valueOf(ilight+1));


                                            String posTask = String.valueOf(document.getData().get("posTask"));
                                            int ipos = Integer.parseInt(posTask);
                                            dailyUpdate.update("posTask",String.valueOf(ipos+1));
                                            getDailyTask();


                                        }
                                    }
                                }
                            });



                            /*******daily********/

                            //update light
                            /*********get**********/
                            statisticUD.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("Data789", "DocumentSnapshot data: " + document.getData().get("light"));
                                            String getData = (String) document.getData().get("light");
                                            int dataUD = Integer.parseInt(getData);
                                            //Toast.makeText(MainActivity.this,"Light Data = " + getData,Toast.LENGTH_SHORT).show();
                                            /********Update********/
                                            statisticUD
                                                    .update("light", String.valueOf(dataUD + 1))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Result", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Result", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            plantData
                                                    .update("light", String.valueOf(dataUD + 5))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("PlantData", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            String getlData = (String) document.getData().get("behavior");
                                            int datalUD = Integer.parseInt(getlData);
                                            /********Update********/
                                            statisticUD
                                                    .update("behavior", String.valueOf(datalUD + 1))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Result", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Result", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            plantData
                                                    .update("fertilizer", String.valueOf(datalUD + 5))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("PlantData", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            /********Update********/

                                            /***********time ************/
                                            userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        Map<String,Object> userData = new HashMap<>();
                                                        userData.put("notiType", String.valueOf(notiKey));
                                                        userData.put("week",currentWeek);
                                                        userData.put("year",currentYear);
                                                        userData.put("month",currentMonth);
                                                        userData.put("dayofyear",currentDay);
                                                        userData.put("dateName",dayOfTheWeek);
                                                        userRef.document(strDate).set(userData);
                                                    }
                                                }
                                            });
                                            /***********time ************/


                                        } else {
                                            Log.d("Data789", "No such document");
                                        }
                                    } else {
                                        Log.d("Data789", "get failed with ", task.getException());
                                    }
                                }
                            });
                            /*********get*********/
                            //end of if(notiKey == 2)
                        }else if (notiKey == 3){
                            /*******daily********/


                            daily.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            String posTask = String.valueOf(document.getData().get("posTask"));
                                            int ipos = Integer.parseInt(posTask);
                                            dailyUpdate.update("posTask",String.valueOf(ipos+1));
                                            getDailyTask();

                                        }
                                    }
                                }
                            });

                            /*******daily********/

                            /*********get**********/
                            statisticUD.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("Data789", "DocumentSnapshot data: " + document.getData().get("behavior"));
                                            String getData = (String) document.getData().get("behavior");
                                            int dataUD = Integer.parseInt(getData);
                                            /********Update********/
                                            statisticUD
                                                    .update("behavior", String.valueOf(dataUD + 1))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Result", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Result", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            plantData
                                                    .update("fertilizer", String.valueOf(dataUD + 5))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("PlantData", "Error updating document", e);
                                                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                            /********Update********/

                                            /***********time ************/
                                            userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        Map<String,Object> userData = new HashMap<>();
                                                        userData.put("notiType", String.valueOf(notiKey));
                                                        userData.put("week",currentWeek);
                                                        userData.put("year",currentYear);
                                                        userData.put("month",currentMonth);
                                                        userData.put("dayofyear",currentDay);
                                                        userData.put("dateName",dayOfTheWeek);
                                                        userRef.document(strDate).set(userData);
                                                    }
                                                }
                                            });
                                            /***********time ************/
                                        } else {
                                            Log.d("Data789", "No such document");
                                        }
                                    } else {
                                        Log.d("Data789", "get failed with ", task.getException());
                                    }
                                }
                            });
                            /*********get*********/

                        }//end of if(notiKey == 3)

                    }//onClick
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do something
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

/****push**/

            }catch(Exception e){
                Log.d("ss" , "Take : .. " + e.getMessage());
            }
        }
    }

    private void getDailyTask(){





        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");

        final CollectionReference daily = db.collection("dailyTask");
        final DocumentReference dailyUpdate = db.collection("dailyTask").document(mail1);


        dailyUpdate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                    String currentDate = dateFormat.format(calendar.getTime());

                    DocumentSnapshot document = task.getResult();

                    String serverDate = String.valueOf(document.getData().get("currentDate"));

                    if (currentDate.equals(serverDate)){



                        String waterTask = String.valueOf(document.getData().get("waterTask"));
                        waterDaily.setText(waterTask);
                        String eyeTask = String.valueOf(document.getData().get("eyeTask"));
                        eyeDaily.setText(eyeTask);
                        String posTask = String.valueOf(document.getData().get("posTask"));
                        posDaily.setText(posTask);


                        Log.d("TESTDAILY",waterTask +"||" + eyeTask + "||" +posTask);
                    }else if(currentDate != serverDate){
                        waterDaily.setText("0");
                        eyeDaily.setText("0");
                        posDaily.setText("0");
                        dailyUpdate.update("currentDate",currentDate);
                        dailyUpdate.update("waterTask","0");
                        dailyUpdate.update("eyeTask","0");
                        dailyUpdate.update("posTask","0");



                    }
                }
            }
        });
/*
        daily.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                    String currentDate = dateFormat.format(calendar.getTime());

                    for (QueryDocumentSnapshot document : task.getResult()) {
                       String serverDate = String.valueOf(document.getData().get("currentDate"));

                        Log.d("CheckDate",currentDate +"||||||||||" +serverDate);

                       if (currentDate.equals(serverDate)){



                           String waterTask = String.valueOf(document.getData().get("waterTask"));
                           waterDaily.setText(waterTask);
                           String eyeTask = String.valueOf(document.getData().get("eyeTask"));
                           eyeDaily.setText(eyeTask);
                           String posTask = String.valueOf(document.getData().get("posTask"));
                           posDaily.setText(posTask);


                           Log.d("TESTDAILY",waterTask +"||" + eyeTask + "||" +posTask);
                       }else if(currentDate != serverDate){
                            waterDaily.setText("0");
                            eyeDaily.setText("0");
                            posDaily.setText("0");
                            dailyUpdate.update("currentDate",currentDate);
                            dailyUpdate.update("waterTask","0");
                            dailyUpdate.update("eyeTask","0");
                            dailyUpdate.update("posTask","0");





                        }
                    }
                }
            }
        });*/
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_main:

                break;
            case R.id.nav_notificaion:
                Intent notiIntent = new Intent(MainActivity.this,ReminderActivityV2.class);
                startActivity(notiIntent);

                break;
            case R.id.nav_statistic:
                Intent statisticIntent = new Intent(MainActivity.this,Statistic.class);
                startActivity(statisticIntent);

                break;
            case R.id.nav_plant:
                Intent plantIntent = new Intent(MainActivity.this,Plant.class);
                startActivity(plantIntent);
                ;
                break;
           /*case R.id.nav_setting:
                Intent settingIntent = new Intent(MainActivity.this,Notification.class);
                startActivity(settingIntent);
                break;*/
            case R.id.nav_logout:

                TwitterCore.getInstance().getSessionManager().clearActiveSession();

                FirebaseAuth.getInstance().signOut();
                getApplicationContext().getSharedPreferences("userInfo", 0).edit().clear().commit();
                getApplicationContext().getSharedPreferences("chartData", 0).edit().clear().commit();
                getApplicationContext().getSharedPreferences("waterData", 0).edit().clear().commit();
                getApplicationContext().getSharedPreferences("lightData", 0).edit().clear().commit();

                LoginManager.getInstance().logOut();
                signOut();
                messaging.unsubscribeFromTopic("thraithepProject");




                Intent logOut = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(logOut);

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        mGoogleSignInClient.revokeAccess();
                    }
                });


    }
}
