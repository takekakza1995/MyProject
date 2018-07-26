package com.example.takethraithip.myproject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RotationActivity extends AppCompatActivity {

    EditText dateName,dayofyear,month,notiType,week,year,docName;
    String getdateName,getdayofyear,getmonth,getnotiType,getweek,getyear,getdocName;
    Button addCF;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation);


        dateName = (EditText) findViewById(R.id.dateNameTxt);
        dayofyear = (EditText) findViewById(R.id.doyTxt);
        month = (EditText) findViewById(R.id.monthTxt);
        notiType = (EditText) findViewById(R.id.notiTypeTxt);
        week = (EditText) findViewById(R.id.weekTxt);
        year = (EditText) findViewById(R.id.yearTxt);
        docName = (EditText) findViewById(R.id.docNameTxt);

        addCF = (Button) findViewById(R.id.addCFbtn);

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        final String mail1 = sharedPreferences.getString("email","not found");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");// for Doc name
        final String strDate = mdformat.format(calendar.getTime()); // for Doc name

        docName.setText(strDate);
        year.setText("2018");
        dayofyear.setText("111");
        dateName.setText("Sunday");
        notiType.setText("1");
        week.setText("29");
        month.setText("7");
        /************************/
        addCF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              getdateName = String.valueOf(dateName.getText());
              getdayofyear = String.valueOf(dayofyear.getText());
              final int doy = Integer.parseInt(getdayofyear);

              getmonth = String.valueOf(month.getText());
              final int monthi = Integer.parseInt(getmonth);

              getnotiType = String.valueOf(notiType.getText());

              getweek = String.valueOf(week.getText());
              final int weeki = Integer.parseInt(getweek);

              getyear = String.valueOf(year.getText());
              final int yeari = Integer.parseInt(getyear);
              getdocName = String.valueOf(docName.getText());

                final CollectionReference userRef = db.collection("users").document(mail1).collection("notiResult");


                Log.d("TESTCFADD",getdateName + "\n" +getdayofyear +"\n" + getmonth + "\n" +getnotiType +"\n"
                + getweek +"\n" + getyear +"\n" + getdocName);
                userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Map<String,Object> userData = new HashMap<>();
                            userData.put("notiType", String.valueOf(getnotiType));
                            userData.put("week",weeki);
                            userData.put("year",yeari);
                            userData.put("month",monthi);
                            userData.put("dayofyear",doy);
                            userData.put("dateName",getdateName);
                            userRef.document(getdocName).set(userData);

                            reload();
                        }
                    }
                });

            }
        });
    }

    private void reload() {
        Intent intent = new Intent(RotationActivity.this,RotationActivity.class);
        startActivity(intent);
        finish();
    }


}
