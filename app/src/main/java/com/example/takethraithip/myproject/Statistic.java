package com.example.takethraithip.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Statistic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView navUserName,navUserMail,testValue,waterDaily,wStandard,lStandard,wUser,lUser;
    ImageView navProfilePic;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    GoogleSignInClient mGoogleSignInClient;
    BarChart barChart,dailyChart;
    PieChart waterPie,lightPie;

    FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    final static String TAG = "CheckError";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /******offline********/
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

/**********/
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        cal.getFirstDayOfWeek();



        /*******nav********/
        View navHead = navigationView.getHeaderView(0);
        navUserName = (TextView) navHead.findViewById(R.id.userName) ;
        navUserMail = (TextView) navHead.findViewById(R.id.email);
        navProfilePic = (ImageView) navHead.findViewById(R.id.profile_picture);

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String name1 = sharedPreferences.getString("name","not Found");
        final String mail1 = sharedPreferences.getString("email","not found");
        String url1 = sharedPreferences.getString("pic","notfound");

        navUserName.setText(name1);
        navUserMail.setText(mail1);
        Picasso.with(Statistic.this).load(url1.toString()).into(navProfilePic);

        /*******nav******/


        loadChartData();//get data from firestore
       // createChart(); //chart all stat
        pieDailyTask();
        Calendar calender = Calendar.getInstance();
        final int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);



    }//on Create

    private void loadChartData() {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");


        Calendar calender = Calendar.getInstance();
        final int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);
        int currentYears = calender.get(Calendar.YEAR);
        int dayOfYear = calender.get(Calendar.DAY_OF_YEAR);

        int oneWeekAgo = currentWeek - 1 ;
        int twoWeekAgo = currentWeek - 2 ;
        int threeWekAho = currentWeek - 3 ;

        final CollectionReference getWaterThisWeek;
        final CollectionReference getWaterOneAgo;
        final CollectionReference getWaterTwoAgo;
        final CollectionReference getWaterDay;

        final CollectionReference getLightThisWeek;
        final CollectionReference getLightOneAgo;
        final CollectionReference getLightTwoAgo;

        final CollectionReference weeklyChart = db.collection("weeklyChart");
        final DocumentReference weeklyChartUpdate = db.collection("weeklyChart").document(mail1);


/******************get water count*********************/
/*******this week*******/
        getWaterThisWeek = db.collection("users").document(mail1).collection("notiResult");

        getWaterThisWeek
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("OfflineCheck", "Listen error", e);
                            return;
                        }

                        for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                            }

                            String source = querySnapshot.getMetadata().isFromCache() ?
                                    "local cache" : "server";
                            Log.d("OfflineCheck", "Data fetched from " + source);
                        }

                    }
                });


        getWaterThisWeek.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears)
                .whereEqualTo("notiType","1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                            if (task.isSuccessful()) {

                               final int waterSize = task.getResult().size();
                            /**********/
                                weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        weeklyChartUpdate.update("water0",waterSize);
                                    }
                                });
                            /**************/

                                Log.d("DataCount","Water size have V3 " );
                            } else {
                                Log.d("GetData555", "Error getting documents: ", task.getException());
                            }
                    }
                });
        /*******this week*******/


        /*******one ago*******/
        getWaterOneAgo = db.collection("users").document(mail1).collection("notiResult");


        getWaterOneAgo.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getWaterOneAgo.whereEqualTo("week",oneWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            final int waterOneAgo = task.getResult().size();

                            weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    weeklyChartUpdate.update("water1",waterOneAgo);
                                }
                            });


                            Log.d("DataCount","Water 1ago have" + waterOneAgo);
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*******one ago*******/

        /*******two ago*******/
        getWaterTwoAgo = db.collection("users").document(mail1).collection("notiResult");

        getWaterTwoAgo.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getWaterTwoAgo.whereEqualTo("week",twoWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            final int waterTwoAgo = task.getResult().size();

                            weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    weeklyChartUpdate.update("water2",waterTwoAgo);
                                }
                            });


                            Log.d("DataCount","Water 2ago have" + waterTwoAgo);
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*******two ago*******/

        /****************** get water count *********************/

        /****** get light count ***********/

        /*********light this week**********/
        getLightThisWeek = db.collection("users").document(mail1).collection("notiResult");

        getLightThisWeek.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getLightThisWeek.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                           final int lightSize = task.getResult().size();

                            weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    weeklyChartUpdate.update("light0",lightSize);
                                }
                            });

                            Log.d("DataCount","Light have" + String.valueOf(lightSize));
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*********light this week**********/

        /*********light one ago**********/
        getLightOneAgo = db.collection("users").document(mail1).collection("notiResult");


        getLightOneAgo.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });


        getLightOneAgo.whereEqualTo("week",oneWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int lightOneAgo = task.getResult().size();

                            weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    weeklyChartUpdate.update("light1",lightOneAgo);
                                }
                            });

                            Log.d("DataCount","Light1 have" + String.valueOf(lightOneAgo));
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*********light one ago**********/

        /******light two ago*****/
        getLightTwoAgo = db.collection("users").document(mail1).collection("notiResult");

        getLightTwoAgo.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getLightTwoAgo.whereEqualTo("week",twoWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int lightTwoAgo = task.getResult().size();

                            weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    weeklyChartUpdate.update("light2",lightTwoAgo);
                                }
                            });

                            Log.d("DataCount","Light1 have" + String.valueOf(lightTwoAgo));
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /******light two ago*****/

        /*******get light count**********/


            weeklyChartUpdate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    String w1 = String.valueOf(documentSnapshot.getData().get("water0"));
                    int water1 = Integer.parseInt(w1);

                    String w2 = String.valueOf(documentSnapshot.getData().get("water1"));
                    int water2 = Integer.parseInt(w2);

                    String w3 = String.valueOf(documentSnapshot.getData().get("water2"));
                    int water3 = Integer.parseInt(w3);

                    String l1 = String.valueOf(documentSnapshot.getData().get("light0"));
                    int light1 = Integer.parseInt(l1);

                    String l2 = String.valueOf(documentSnapshot.getData().get("light1"));
                    int light2 = Integer.parseInt(l2);

                    String l3 = String.valueOf(documentSnapshot.getData().get("light2"));
                    int light3 = Integer.parseInt(l3);


                    createChart(water1,water2,water3,
                            light1,light2,light3);
                }
            });




        /********get*******/



        //ferChartValue = sharedPreferences.getInt("ferValue",0);

    }

    private void pieDailyTask(){
        Calendar calendar;
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        final String currentDate = dateFormat.format(calendar.getTime());


        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");

        final CollectionReference daily = db.collection("dailyTask");
        final DocumentReference weeklyPie = db.collection("weeklyChart").document(mail1);


        /****/
        weeklyPie.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                        String waterTask = String.valueOf(document.getData().get("water1"));

                        String eyeTask = String.valueOf(document.getData().get("light1"));
                        float wPieValue = Integer.parseInt(waterTask);
                        float moreW = 56 - wPieValue;
                        float ePieValue = Integer.parseInt(eyeTask) ;
                        float moreE = 28- ePieValue;

                    waterPie = (PieChart) findViewById(R.id.waterPie);
                    ArrayList<Entry> piewEntry = new ArrayList<>();
                    ArrayList<String> labelPie = new ArrayList<>();
                    if (moreW ==0 ){
                        piewEntry.add(new Entry(wPieValue,0));
                        labelPie.add("Success");
                    }else if (moreW != 0){
                        piewEntry.add(new Entry(wPieValue,0));
                        piewEntry.add(new Entry(moreW,1));
                        labelPie.add("You");
                        labelPie.add("More");
                    }


                    PieDataSet pieDataSet = new PieDataSet(piewEntry,"");
                    pieDataSet.setValueTextSize(16);
                    pieDataSet.setValueTextColor(Color.WHITE);
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);




                    PieData data = new PieData(labelPie, pieDataSet);
                    waterPie.setData(data);
                    waterPie.setDescription("");

                    waterPie.animateY(2000);


                    lightPie = (PieChart) findViewById(R.id.lightPie);
                    ArrayList<Entry> lightEntry = new ArrayList<>();
                    ArrayList<String> labelPieLight = new ArrayList<>();
                    if (moreE ==0){
                        lightEntry.add(new Entry(ePieValue,0));
                        labelPieLight.add("Success");
                    }else if (moreE != 0){
                        lightEntry.add(new Entry(ePieValue,0));
                        lightEntry.add(new Entry(moreE,1));
                        labelPieLight.add("You");
                        labelPieLight.add("More");
                    }

                    // lightEntry.add(new Entry(ePieValue,0));
                    // lightEntry.add(new Entry(moreE,1));
                    PieDataSet LightpieDataSet = new PieDataSet(lightEntry,"");
                    LightpieDataSet.setValueTextSize(16);
                    //LightpieDataSet.setValueTextColor(Color.WHITE);
                    LightpieDataSet.setColors(ColorTemplate.LIBERTY_COLORS);




                    PieData dataLight = new PieData(labelPieLight, LightpieDataSet);
                    lightPie.setData(dataLight);
                    lightPie.setDescription("");
                    lightPie.animateY(2000);
                }
            }
        });
        /****/

        daily.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "New city:" + change.getDocument().getData());
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d(TAG, "Data fetched from " + source);
                }

            }
        });


/*
        daily.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                       //String serverDate = String.valueOf(document.getData().get("currentDate"));

                            String waterTask = String.valueOf(document.getData().get("waterTask"));

                            String eyeTask = String.valueOf(document.getData().get("eyeTask"));
                            float wPieValue = Integer.parseInt(waterTask);
                            float moreW = 8 - wPieValue;
                            float ePieValue = Integer.parseInt(eyeTask) ;
                            float moreE = 4- ePieValue;


                        waterPie = (PieChart) findViewById(R.id.waterPie);
                        ArrayList<Entry> piewEntry = new ArrayList<>();
                        ArrayList<String> labelPie = new ArrayList<>();
                        if (moreW ==0 ){
                            piewEntry.add(new Entry(wPieValue,0));
                            labelPie.add("Success");
                        }else if (moreW != 0){
                            piewEntry.add(new Entry(wPieValue,0));
                            piewEntry.add(new Entry(moreW,1));
                            labelPie.add("You");
                            labelPie.add("More");
                        }


                        PieDataSet pieDataSet = new PieDataSet(piewEntry,"");
                        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);




                        PieData data = new PieData(labelPie, pieDataSet);
                        waterPie.setData(data);
                        waterPie.setDescription("");

                        waterPie.animateY(2000);


                        lightPie = (PieChart) findViewById(R.id.lightPie);
                        ArrayList<Entry> lightEntry = new ArrayList<>();
                        ArrayList<String> labelPieLight = new ArrayList<>();
                        if (moreE ==0){
                            lightEntry.add(new Entry(ePieValue,0));
                            labelPieLight.add("Success");
                        }else if (moreE != 0){
                            lightEntry.add(new Entry(ePieValue,0));
                            lightEntry.add(new Entry(moreE,1));
                            labelPieLight.add("You");
                            labelPieLight.add("More");
                        }

                       // lightEntry.add(new Entry(ePieValue,0));
                       // lightEntry.add(new Entry(moreE,1));
                        PieDataSet LightpieDataSet = new PieDataSet(lightEntry,"");
                        LightpieDataSet.setColors(ColorTemplate.LIBERTY_COLORS);




                        PieData dataLight = new PieData(labelPieLight, LightpieDataSet);
                        lightPie.setData(dataLight);
                        lightPie.setDescription("");
                        lightPie.animateY(2000);

                    }
                }
            }
        });*/

        daily.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });


    }

    private void createChart(int water1,int water2,int water3,int light1,int light2,int light3) {
        /******chart******/
        Calendar calender = Calendar.getInstance();
        final int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);
        final int oneWeekAgo = currentWeek - 1 ;
        final int twoWeekAgo = currentWeek - 2 ;


        barChart = (BarChart) findViewById(R.id.statBarchart);

/******************/
        dailyChart = (BarChart) findViewById(R.id.dailyBarchart) ;

/***********************/
        dailyChart.setVisibility(View.GONE);

        ArrayList<BarEntry> waterBar = new ArrayList<>();
        waterBar.add(new BarEntry(water3,0)); //data week 1
        waterBar.add(new BarEntry(water2,1));   //data week 2
        waterBar.add(new BarEntry(water1,2));   //data week 3
        //waterBar.add(new BarEntry(8,3));   //data week 4

        ArrayList<BarEntry> lightBar = new ArrayList<>();
        lightBar.add(new BarEntry(light3,0)); //data week 1
        lightBar.add(new BarEntry(light2,1));   //data week 2
        lightBar.add(new BarEntry(light1,2));   //data week 3
        //lightBar.add(new BarEntry(4,3));   //data week 4

        ArrayList<String> label = new ArrayList<>();

        label.add("Two week ago");
        label.add("One week ago");
        label.add("This Week");
        // label.add("Three week ago");

        BarDataSet barWaterSet = new BarDataSet(waterBar,"Water");
        barWaterSet.setColor(Color.rgb(110,235,255));
        barWaterSet.setValueTextSize(12);

        BarDataSet barLightSet = new BarDataSet(lightBar,"Light");
        barLightSet.setColor(Color.rgb(255,255,100));
        barLightSet.setValueTextSize(12);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barWaterSet);
        dataSets.add(barLightSet);

        BarData barData = new BarData(label,dataSets);
        barChart.setDescription("");
        barChart.animateY(2000);
        barChart.setData(barData);
        barChart.getData().setHighlightEnabled(false);



        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //entrt index//datasetindex
                barChart.setVisibility(View.INVISIBLE);
                int indexCheck = e.getXIndex();
                //int dataSetIn = dataSetIndex;

                if (indexCheck == 0 && dataSetIndex ==0){//water daily chart two week ago
                    int type = 1;
                    String weekid = "waterW2";
                    String notiType = String.valueOf(type);
                    getDayOfWeekChartData(twoWeekAgo,notiType,weekid);

                    //  createDailyChart(5,4,8,9,3,4,1,type);

                }

                else if (indexCheck == 0 && dataSetIndex == 1){//light daily chart two week ago
                    int type = 2;
                    String weekid = "lightW2";
                    String notiType = String.valueOf(type);
                    getDayOfWeekChartData(twoWeekAgo,notiType,weekid);
                    // createDailyChart(9,8,7,6,5,4,3,type);

                }

                else if (indexCheck == 1 && dataSetIndex ==0){//water daily chart one week ago
                    int type = 1;
                    String weekid = "waterW1";
                    String notiType = String.valueOf(type);
                    getDayOfWeekChartData(oneWeekAgo,notiType,weekid);
                    // createDailyChart(1,2,3,4,5,6,7,type);
                }

                else if (indexCheck == 1 && dataSetIndex ==1){//light daily chart one week ago
                    int type = 2;
                    String weekid = "lightW1";
                    String notiType = String.valueOf(type);
                    getDayOfWeekChartData(oneWeekAgo,notiType,weekid);
                    //  createDailyChart(10,4,9,3,1,5,2,type);
                }

                else if (indexCheck == 2 && dataSetIndex ==0){//water daily chart this week
                    String weekid = "waterW0";
                    int type = 1;
                    String notiType = String.valueOf(type);
                    getDayOfWeekChartData(currentWeek,notiType,weekid);
                    // createDailyChart(3,2,8,4,5,1,6,type);
                }
                else if (indexCheck == 2 && dataSetIndex ==1){//light daily chart this week
                    int type = 2;
                    String weekid = "lightW0";
                    String notiType = String.valueOf(type);
                    getDayOfWeekChartData(currentWeek,notiType,weekid);
                    // createDailyChart(5,5,5,5,5,5,5,type);
                }


                Log.d("ChartClick", String.valueOf(indexCheck) +"|||" +dataSetIndex);
                dailyChart.setVisibility(View.VISIBLE);
                dailyChart.setDescription("");

            }

            @Override
            public void onNothingSelected() {

            }
        });

        dailyChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dailyChart.clearValues();
                dailyChart.setVisibility(View.INVISIBLE);
                barChart.setVisibility(View.VISIBLE);
                barChart.animateY(2000);
            }
        });


        /**************************/



        /********************************/
    }


    private void createDailyChart(int monVal,int tueVal,int wedVal,int thuVal,
                                  int friVal,int satVal,int sunVal,int type){

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");

        final CollectionReference getDate = db.collection("users")
                .document(mail1).collection("notiResult");

        getDate.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String day = String.valueOf(document.getData().get("dayofyear"));
                        String month = String.valueOf(document.getData().get("month"));
                        String year = String.valueOf(document.getData().get("year"));

                        String date = day+"/"+month+"/"+year;
                    }
                }


            }
        });

        ArrayList<BarEntry> dailyBar = new ArrayList<>();
        dailyBar.add(new BarEntry(sunVal,0));
        dailyBar.add(new BarEntry(monVal,1)); //data week 1
        dailyBar.add(new BarEntry(tueVal,2));   //data week 2
        dailyBar.add(new BarEntry(wedVal,3));
        dailyBar.add(new BarEntry(thuVal,4));
        dailyBar.add(new BarEntry(friVal,5));
        dailyBar.add(new BarEntry(satVal,6));


        ArrayList<String> dailylabel = new ArrayList<>();
        dailylabel.add("Sun");
        dailylabel.add("Mon");
        dailylabel.add("Tue");
        dailylabel.add("Wed");
        dailylabel.add("Thu");
        dailylabel.add("Fri");
        dailylabel.add("Sat");


        if (type ==1) {
            BarDataSet dailyDataset = new BarDataSet(dailyBar, "water");
            dailyDataset.setColor(Color.rgb(110, 235, 255));
           // dailyDataset.setColors(ColorTemplate.LIBERTY_COLORS);
            BarData dailyData = new BarData(dailylabel,dailyDataset);
            dailyChart.animateY(2000);
            dailyChart.setData(dailyData);
            dailyChart.setDescription("");
            dailyChart.getData().setHighlightEnabled(false);

        }else if (type ==2 ){
            BarDataSet dailyDataset = new BarDataSet(dailyBar, "light");
            dailyDataset.setColor(Color.rgb(255,255,100));
            //dailyDataset.setColors(ColorTemplate.JOYFUL_COLORS);
            BarData dailyData = new BarData(dailylabel,dailyDataset);
            dailyChart.animateY(2000);
            dailyChart.setData(dailyData);
            dailyChart.setDescription("");
            dailyChart.getData().setHighlightEnabled(false);
        }


    }



    private void getDayOfWeekChartData(final int week, final String getType, String id){
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");


        Calendar calender = Calendar.getInstance();
        int currentYears = calender.get(Calendar.YEAR);

        String weekOfId = "";


        final CollectionReference getMon;
        final CollectionReference getTue;
        final CollectionReference getWed;
        final CollectionReference getThu;
        final CollectionReference getFri;
        final CollectionReference getSat;
        final CollectionReference getSun;

        final CollectionReference weeklyChart = db.collection("weeklyChart");



        if (id.equals("waterW0")){

            weekOfId = "waterWeek0";

        }else if (id.equals("waterW1")){

            weekOfId = "waterWeek1";

        }else if (id.equals("waterW2")){

            weekOfId = "waterWeek2";

        }else if (id.equals("lightW0")){

            weekOfId = "lightWeek0";

        }else if (id.equals("lightW1")){

            weekOfId = "lightWeek1";

        }else if (id.equals("lightW2")){

            weekOfId = "lightWeek2";
        }


        final DocumentReference getWeekly = db.collection("weeklyChart").document(mail1)
                .collection(weekOfId).document(mail1);


        weeklyChart.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d(TAG, "Data fetched from " + source);
                }

            }
        });

        getMon = db.collection("users").document(mail1).collection("notiResult");
        getMon.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Monday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final int monVal = task.getResult().size();


                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("mon",monVal);
                        }
                    });
                }
            }
        });

        getMon.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getTue = db.collection("users").document(mail1).collection("notiResult");
        getTue.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Tuesday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final int tueVal = task.getResult().size();

                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("tue",tueVal);
                        }
                    });

                }
            }
        });

        getTue.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getWed = db.collection("users").document(mail1).collection("notiResult");
        getWed.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Wednesday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final int wedVal = task.getResult().size();

                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("wed",wedVal);
                        }
                    });
                }
            }
        });

        getWed.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getThu= db.collection("users").document(mail1).collection("notiResult");
        getThu.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Thursday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final int thuVal = task.getResult().size();

                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("thu",thuVal);
                        }
                    });
                }
            }
        });

        getThu.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getFri = db.collection("users").document(mail1).collection("notiResult");
        getFri.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Friday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final int friVal = task.getResult().size();

                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("fri",friVal);
                        }
                    });
                }
            }
        });

        getFri.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getSat= db.collection("users").document(mail1).collection("notiResult");
        getSat.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Saturday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                   final int satVal = task.getResult().size();



                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("sat",satVal);
                        }
                    });
                }
            }
        });

        getSat.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });

        getSun= db.collection("users").document(mail1).collection("notiResult");
        getSun.whereEqualTo("week",week).whereEqualTo("year",currentYears)
                .whereEqualTo("dateName","Sunday").whereEqualTo("notiType",getType)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final int sunVal = task.getResult().size();

                    weeklyChart.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            getWeekly.update("sun",sunVal);
                        }
                    });
                }
            }
        });

        getSun.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("OfflineCheck", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("OfflineCheck", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("OfflineCheck", "Data fetched from " + source);
                }

            }
        });


        //final DocumentReference getWeekly = db.collection("weekChart").document(mail1)
        //                .collection(weekOfId).document(mail1);


        getWeekly.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot getDoc = task.getResult();

                String getmon = String.valueOf(getDoc.getData().get("mon"));
                int getMonVal = Integer.parseInt(getmon);

                String gettue = String.valueOf(getDoc.getData().get("tue"));
                int getTueVal = Integer.parseInt(gettue);

                String getwed = String.valueOf(getDoc.getData().get("wed"));
                int getWedVal = Integer.parseInt(getwed);

                String getthu = String.valueOf(getDoc.getData().get("thu"));
                int getThuVal = Integer.parseInt(getthu);

                String getfri = String.valueOf(getDoc.getData().get("fri"));
                int getFriVal = Integer.parseInt(getfri);

                String getsat = String.valueOf(getDoc.getData().get("sat"));
                int getSatVal = Integer.parseInt(getsat);

                String getsun = String.valueOf(getDoc.getData().get("sun"));
                int getSunVal = Integer.parseInt(getsun);

                Log.d("CheckGetVal" , getsat);

                createDailyChart(getMonVal,getTueVal,getWedVal,
                        getThuVal,getFriVal,getSatVal,getSunVal,Integer.parseInt(getType));

            }
        });


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
                Intent homeIntent = new Intent(Statistic.this,MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.nav_notificaion:
                Intent notiIntent = new Intent(Statistic.this,ReminderActivityV2.class);
                startActivity(notiIntent);
                finish();
                break;
            case R.id.nav_statistic:

                break;
            case R.id.nav_plant:
                Intent plantIntent = new Intent(Statistic.this,Plant.class);
                startActivity(plantIntent);
                finish();
                break;
           /* case R.id.nav_setting:
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




                Intent logOut = new Intent(Statistic.this,LoginActivity.class);
                startActivity(logOut);


                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        if (mGoogleSignInClient != null) {
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
}
