package com.example.takethraithip.myproject;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        /*CollectionReference coll = db.collection("users").document(mail1).collection("notiResult");
        coll.getId().startsWith("25-05-2018");*/

        loadChartData();//get data from firestore
        createChart(); //chart all stat
        testValue = (TextView)findViewById(R.id.testValue);
        testValue.setText("Data from FireStore =>" + String.valueOf(waterChartValue)+":"
                +String.valueOf(lightChartValue)+":"+String.valueOf(ferChartValue));


        Button lightButton,waterButton;
        lightButton = (Button) findViewById(R.id.Lbtn);
        waterButton = (Button) findViewById(R.id.wBtn);

        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lIntent = new Intent(Statistic.this,LightChart.class);
                startActivity(lIntent);



            }
        });

        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show graph
                Intent intent = new Intent(Statistic.this,WaterChart.class);
                startActivity(intent);


            }
        });



    }//on Create

    private void loadChartData() {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");
        /******************get water count*********************/

        Calendar calender = Calendar.getInstance();
        final int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);
        int currentYears = calender.get(Calendar.YEAR);
        int oneWeekAgo = currentWeek -1 ;
        int twoWeekAgo = currentWeek -2 ;

        final CollectionReference getWaterCount = db.collection("users").document(mail1).collection("notiResult");
        getWaterCount.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears).whereEqualTo("notiType","1")
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
        getLightCount.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
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
        getFerCount.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears).whereEqualTo("notiType","3")
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
    }

    private void createChart() {
        /******chart******/

        barChart = (BarChart) findViewById(R.id.statBarchart);

        ArrayList<BarEntry> waterBar = new ArrayList<>();
        waterBar.add(new BarEntry(waterChartValue,0)); //data week 1
        waterBar.add(new BarEntry(5,1));   //data week 2
        waterBar.add(new BarEntry(2,2));   //data week 3
        //waterBar.add(new BarEntry(8,3));   //data week 4

        ArrayList<BarEntry> lightBar = new ArrayList<>();
        lightBar.add(new BarEntry(lightChartValue,0)); //data week 1
        lightBar.add(new BarEntry(3,1));   //data week 2
        lightBar.add(new BarEntry(7,2));   //data week 3
        //lightBar.add(new BarEntry(4,3));   //data week 4

        ArrayList<String> label = new ArrayList<>();
        label.add("This Week");
        label.add("One week ago");
        label.add("Two week ago");
       // label.add("Three week ago");

        BarDataSet barWaterSet = new BarDataSet(waterBar,"Water");
        barWaterSet.setColor(Color.rgb(110,235,255));

        BarDataSet barLightSet = new BarDataSet(lightBar,"Light");
        barLightSet.setColor(Color.rgb(255,255,100));

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barWaterSet);
        dataSets.add(barLightSet);

        BarData barData = new BarData(label,dataSets);
        barChart.setDescription(" All Statistic");
        barChart.animateY(2000);
        barChart.setData(barData);



/*
        barChart = (BarChart) findViewById(R.id.statBarchart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setDescription(null);
        barChart.setPinchZoom(false);
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> waterBar = new ArrayList<>();
        waterBar.add(new BarEntry(0f,waterChartValue)); //data week 1
        waterBar.add(new BarEntry(1f,5));   //data week 2
        waterBar.add(new BarEntry(2f,7));   //data week 3

        ArrayList<BarEntry> lightBar = new ArrayList<>();
        lightBar.add(new BarEntry(0f,lightChartValue)); //data week 1
        lightBar.add(new BarEntry(1f,4));   //data week 2
        lightBar.add(new BarEntry(2f,9));   //data week 3

        ArrayList<BarEntry> behavBar = new ArrayList<>();
        behavBar.add(new BarEntry(0f,ferChartValue));   //data week 1
        behavBar.add(new BarEntry(1f,4));   //data week 2
        behavBar.add(new BarEntry(2f,6));   //data week 3


        int groupCount = 2;
        final ArrayList label = new ArrayList();
        label.add("This Week");*/

       /* label.add("One Week Ago");
        label.add("Two Week Ago");*/

    /*    BarDataSet set1,set2,set3;
        set1 = new BarDataSet(waterBar,"water");
        set1.setColor(Color.rgb(110,235,255));
        set2 = new BarDataSet(lightBar,"light");
        set2.setColor(Color.rgb(255,255,100));
        set3 = new BarDataSet(behavBar,"pos");
        set3.setColor(Color.rgb(100,255,110));*/

     /*   float groupSpace,barSpac,barWidth;
        groupSpace = 1f;
        barSpac =    0.5f;
        barWidth = 0.7f;

        BarData data = new BarData(set1,set2,set3);
        data.setValueFormatter(new LargeValueFormatter());
        barChart.setData(data);
        barChart.getBarData().setBarWidth(0.3f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace,barSpac) * groupCount);
        barChart.groupBars(0,groupSpace,barSpac);
        barChart.getData().setHighlightEnabled(false);
        barChart.invalidate();*/

        //data.setBarWidth(barWidth);
        //barChart.setData(data);
        //barChart.setDescription("All Statistic");
    /*    barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);


        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        //X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(0.5f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(label));

//Y-axis/*
        barChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinimum(0f);


        */

        //barEntries.add(new BarEntry(4,70f));

       /* ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        barEntries1.add(new BarEntry(1,70f));
        barEntries1.add(new BarEntry(2,60f));
        barEntries1.add(new BarEntry(3,50f));
        barEntries1.add(new BarEntry(4,40f));*/

       /* BarDataSet barDataSet = new BarDataSet(barEntries1,"Stat");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);*/




        //BarData data = new BarData(barDataSet);

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
