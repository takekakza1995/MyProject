package com.example.takethraithip.myproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class LightChart extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    int week0Value,week1Value,week2Value;
    BarChart lightChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_chart);

        getData();
        createChart();

        Button bt1 = (Button) findViewById(R.id.testLightData);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d("Check water Data", week0Value + "||||" + week1Value + "||||" + week2Value);
            }
        });

    }//onCreate


    private void getData() {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String userMail = sharedPreferences.getString("email","not found");

        Calendar calender = Calendar.getInstance();
        final int currentWeek = calender.get(Calendar.WEEK_OF_YEAR);
        int currentYears = calender.get(Calendar.YEAR);
        int oneWeekAgo = currentWeek -1 ;
        int twoWeekAgo = currentWeek -2 ;

        Log.d("GetWaterFS","Current week is =>" + currentWeek+"||"+oneWeekAgo+"||"+twoWeekAgo);

        /*****************This week*******************/
        CollectionReference lightWeek0 = db.collection("users").document(userMail).collection("notiResult");
        lightWeek0.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetLightFS", document.getId() + " => " + document.getData());

                        int lightW0 = task.getResult().size();
                        Log.d("GetLightFS", "Light Week0 Size =>" + lightW0);

                        sharedPreferences = getSharedPreferences("lightData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("lightWeek0",lightW0);

                        editor.commit();
                    }
                } else {
                    Log.d("GetLightFS", "Error getting documents: ", task.getException());

                }
            }
        });
        /****************one week ago***************/
        CollectionReference lightWeek1 = db.collection("users").document(userMail).collection("notiResult");
        lightWeek1.whereEqualTo("week",oneWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetLightFS", document.getId() + " => " + document.getData());

                        int lightW1 = task.getResult().size();
                        Log.d("GetLightFS","Light Week 1 =>" + lightW1);

                        sharedPreferences = getSharedPreferences("lightData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putInt("lightWeek1",lightW1);
                        editor.commit();
                    }

                } else {
                    Log.d("GetLightFS", "Error getting documents: ", task.getException());

                }
            }
        });
        /******************two week ago*******************/

        CollectionReference lightWeek2 = db.collection("users").document(userMail).collection("notiResult");
        lightWeek2.whereEqualTo("week",twoWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","2")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetLightFS", document.getId() + " => " + document.getData());

                        int lightW2 = task.getResult().size();
                        Log.d("GetLightFS","Light Week 2 =>" + lightW2);

                        sharedPreferences = getSharedPreferences("lightData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("lightWeek2",lightW2);
                        editor.commit();
                    }
                } else {
                    Log.d("GetLightFS", "Error getting documents: ", task.getException());

                }
            }
        });

        sharedPreferences = getSharedPreferences("lightData",MODE_PRIVATE);
        week0Value = sharedPreferences.getInt("lightWeek0",0);
        week1Value = sharedPreferences.getInt("lightWeek1",0);
        week2Value = sharedPreferences.getInt("lightWeek2",0);
        /******************get data************************/

    }

    private void createChart() {

        lightChart = (BarChart) findViewById(R.id.lightBarchart);
        ArrayList<BarEntry> lightBar = new ArrayList<>();
        lightBar.add(new BarEntry(week0Value, 0)); //data week 1
        lightBar.add(new BarEntry(week1Value, 1));   //data week 2
        lightBar.add(new BarEntry(week2Value, 2));   //data week 3

        BarDataSet barDataSet = new BarDataSet(lightBar,"Light");
        barDataSet.setColor(Color.rgb(255,255,100));
        barDataSet.setBarSpacePercent(50);


        final ArrayList label = new ArrayList();
        label.add("This Week");
        label.add("One week ago");
        label.add("Two week ago");

        BarData barData = new BarData(label,barDataSet);
        lightChart.setVisibleXRange(0,10);
        lightChart.invalidate();
        lightChart.animateY(2000);
        lightChart.setData(barData);


    }

}
