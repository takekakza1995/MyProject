package com.example.takethraithip.myproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class WaterChart extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    int week0Value,week1Value,week2Value;
    BarChart waterChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_chart);


        getData();
        createChart();

        Button bt1 = (Button) findViewById(R.id.testData);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Log.d("Check water Data", week0Value + "||||" + week1Value + "||||" + week2Value);
            }
        });



    }

    private void createChart() {
        waterChart = (BarChart) findViewById(R.id.waterBarchart);
        waterChart.setDrawBarShadow(false);
        waterChart.setDrawValueAboveBar(true);
        //barChart.setMaxVisibleValueCount(50);
        waterChart.setDescription("Water Chart");
        waterChart.setPinchZoom(false);
        //waterChart.setFitBars(true);
        waterChart.setDrawGridBackground(true);

        ArrayList<BarEntry> waterBar = new ArrayList<>();
        waterBar.add(new BarEntry(week2Value,0)); //data week 1
        waterBar.add(new BarEntry(week1Value,1));   //data week 2
        waterBar.add(new BarEntry(week0Value,2));   //data week 3
        //waterBar.add(new BarEntry(6f,3));   //data week 3


        BarDataSet barDataSet = new BarDataSet(waterBar,"Water");
        barDataSet.setBarSpacePercent(50);


        final ArrayList label = new ArrayList();
        label.add("Two week ago");
        label.add("One week ago");
        label.add("This weel");
        //label.add("Three week ago");

        BarData barData = new BarData(label,barDataSet);
        waterChart.setVisibleXRange(0,10);
        waterChart.invalidate();
        waterChart.animateY(2000);
        waterChart.setData(barData);







    }


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
        CollectionReference wateWeek0 = db.collection("users").document(userMail).collection("notiResult");
        wateWeek0.whereEqualTo("week",currentWeek).whereEqualTo("year",currentYears).whereEqualTo("notiType","1")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetWaterFS", document.getId() + " => " + document.getData());

                        int waterW0 = task.getResult().size();
                        Log.d("GetWaterFS", "Water Week0 Size =>" + waterW0);

                        sharedPreferences = getSharedPreferences("waterData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("waterWeek0",waterW0);

                        editor.commit();
                    }
                } else {
                    Log.d("GetTimeFS", "Error getting documents: ", task.getException());

                }
            }
        });
       /****************one week ago***************/
        CollectionReference wateWeek1 = db.collection("users").document(userMail).collection("notiResult");
        wateWeek1.whereEqualTo("week",oneWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","1")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetWaterFS", document.getId() + " => " + document.getData());

                        int waterW1 = task.getResult().size();
                        Log.d("GetWaterFS","Water Week 1 =>" + waterW1);

                        sharedPreferences = getSharedPreferences("waterData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putInt("waterWeek1",waterW1);
                        editor.commit();
                    }

                } else {
                    Log.d("GetTimeFS", "Error getting documents: ", task.getException());

                }
            }
        });
        /******************two week ago*******************/

        CollectionReference wateWeek2 = db.collection("users").document(userMail).collection("notiResult");
        wateWeek2.whereEqualTo("week",twoWeekAgo).whereEqualTo("year",currentYears).whereEqualTo("notiType","1")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetWaterFS", document.getId() + " => " + document.getData());

                        int waterW2 = task.getResult().size();
                        Log.d("GetWaterFS","Water Week 2 =>" + waterW2);

                        sharedPreferences = getSharedPreferences("waterData",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("waterWeek2",waterW2);
                        editor.commit();
                    }
                } else {
                    Log.d("GetTimeFS", "Error getting documents: ", task.getException());

                }
            }
        });

        sharedPreferences = getSharedPreferences("waterData",MODE_PRIVATE);
        week0Value = sharedPreferences.getInt("waterWeek0",0);
        week1Value = sharedPreferences.getInt("waterWeek1",0);
        week2Value = sharedPreferences.getInt("waterWeek2",0);
        /******************get data************************/



    }

}
