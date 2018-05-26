package com.example.takethraithip.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView navUserName,navUserMail,testValue;
    ImageView navProfilePic;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int waterChartValue,lightChartValue,ferChartValue;
    BarChart barChart;





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




        /*******get********/
        /*final DocumentReference statData = db.collection("statistic").document(mail1);

        statData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("GetStatsssss", "DocumentSnapshot data: " + document.getData());

                        String getWater = (String) document.getData().get("water"); // get data from FS
                        waterData = Integer.parseInt(getWater);
                        String getLight = (String) document.getData().get("light"); // get data from FS
                        ligtData = Integer.parseInt(getLight);
                        String getBehav = (String) document.getData().get("behavior"); // get data from FS
                        behavData = Integer.parseInt(getBehav);

                    } else {
                        Log.d("GetStat", "No such document");
                    }
                } else {
                    Log.d("GetStat", "get failed with ", task.getException());
                }
            }
        });*/
            /******************get water count*********************/
        final CollectionReference getWaterCount = db.collection("users").document(mail1).collection("notiResult");
        getWaterCount.whereEqualTo("notiType","1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int waterSize = task.getResult().size();

                            sharedPreferences = getSharedPreferences("chartData",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("waterValue",waterSize);
                            editor.commit();

                            Log.d("DataCount","Water have" + waterSize);
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /****************** get water count *********************/

        /****** get light count ***********/
        final CollectionReference getLightCount = db.collection("users").document(mail1).collection("notiResult");
        getLightCount.whereEqualTo("notiType","2")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int lightSize = task.getResult().size();
                            sharedPreferences = getSharedPreferences("chartData",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("lightValue",lightSize);
                            editor.commit();

                            Log.d("DataCount","Light have" + String.valueOf(lightSize));
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*******get light count**********/

        /********get fer count*********/
        final CollectionReference getFerCount = db.collection("users").document(mail1).collection("notiResult");
        getFerCount.whereEqualTo("notiType","3")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int ferSize = task.getResult().size();
                            sharedPreferences = getSharedPreferences("chartData",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("ferValue",ferSize);
                            editor.commit();

                            Log.d("DataCount","Fertilizer have" + String.valueOf(ferSize));
                        } else {
                            Log.d("GetData555", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*******get fer count**********/


        /********get*******/

        sharedPreferences = getSharedPreferences("chartData",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        waterChartValue = sharedPreferences.getInt("waterValue", 0);
        lightChartValue = sharedPreferences.getInt("lightValue",0);
        ferChartValue = sharedPreferences.getInt("ferValue",0);


        String tt = "";
        testValue = (TextView)findViewById(R.id.testValue);
        testValue.setText(String.valueOf(waterChartValue)+":"
                +String.valueOf(lightChartValue)+":"+String.valueOf(ferChartValue));


        Button lightButton,waterButton;
        lightButton = (Button) findViewById(R.id.Lbtn);
        waterButton = (Button) findViewById(R.id.wBtn);

        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show graph

            }
        });

        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show graph
            }
        });

/******chart******/
        barChart = (BarChart) findViewById(R.id.statBarchart);
        /*barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(true);*/

        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        barEntries1.add(new BarEntry(waterChartValue,0));
        barEntries1.add(new BarEntry(lightChartValue,1));
        barEntries1.add(new BarEntry(ferChartValue,2));

        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
        barEntries2.add(new BarEntry(waterChartValue,0));
        barEntries2.add(new BarEntry(lightChartValue,1));
        barEntries2.add(new BarEntry(ferChartValue,2));

        ArrayList<BarEntry> barEntries3 = new ArrayList<>();
        barEntries3.add(new BarEntry(waterChartValue,0));
        barEntries3.add(new BarEntry(lightChartValue,1));
        barEntries3.add(new BarEntry(ferChartValue,2));

        //barEntries.add(new BarEntry(4,70f));

       /* ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        barEntries1.add(new BarEntry(1,70f));
        barEntries1.add(new BarEntry(2,60f));
        barEntries1.add(new BarEntry(3,50f));
        barEntries1.add(new BarEntry(4,40f));*/

        BarDataSet barDataSet = new BarDataSet(barEntries1,"Stat");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<String> label = new ArrayList<>();
        label.add("This Week");
        label.add("One Week Ago");
        label.add("Two Week Ago");


        BarData data = new BarData(label,barDataSet);

        barChart.setData(data);
        //barChart.setDescription("All Statistic");
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);

        /*BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Data set2");
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);*/

        //BarData data = new BarData(barDataSet);

        //float groupSpace = 0.1f;
       // float barSpace = 0.02f;
        //float barWidth = 0.50f;

        //data.setBarWidth(barWidth);
        //barChart.groupBars(0,groupSpace,barSpace);


/*
        String[] month = new String[] {"Jan","Feb","Mar","April","May","Jun"};
        XAxis axis = barChart.getXAxis();
        axis.setValueFormatter(new MyXAxisValueFormatter(month));
        axis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        axis.setGranularity(1);
        axis.setCenterAxisLabels(true);
        axis.setAxisMinimum(0.1f);
        axis.setAxisMinimum(0.1f);
*/



        /************chart*********/

    }//on Create

    /*************class for chart****************/
     /*   public class MyXAxisValueFormatter implements IAxisValueFormatter{

            private String[] mValues;
        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            return mValues[(int)value];

        }
    }
*/
    /*************class for chart****************/



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                Intent notiIntent = new Intent(Statistic.this,Notification.class);
                startActivity(notiIntent);
                finish();
                break;
            case R.id.nav_statistic:
                Intent statisticIntent = new Intent(Statistic.this,Statistic.class);
                startActivity(statisticIntent);
                finish();
                break;
            case R.id.nav_plant:
                Intent plantIntent = new Intent(Statistic.this,Plant.class);
                startActivity(plantIntent);
                finish();
                break;
           /* case R.id.nav_setting:
                Intent settingIntent = new Intent(MainActivity.this,Notification.class);
                startActivity(settingIntent);
                break;
            case R.id.nav_logout:

                break;    */

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
