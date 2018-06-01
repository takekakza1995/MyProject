package com.example.takethraithip.myproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{


    String sub = "";
    Button addNoti,backNoti;
    int day , month , year , hour , minute;
    int dayFinal , mountFinal , yearFinal , hourFinal, minuteFinal;
    AlarmManager alarmManager;
    PendingIntent alarmIntent;

    ListView lv1;
    ArrayAdapter<String> addAdapter;

    List<notiTyping> timeList = new ArrayList<>();

    SharedPreferences sharedPreferences;
    TextView navUserName,navUserMail;
    ImageView navProfilePic;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<notiTyping> notiTypingList = new ArrayList<notiTyping>();
    private RecyclerView recyclerView;
    private notiAdapter notiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String userName = sharedPreferences.getString("name", "name notFound");
        final String userMail = sharedPreferences.getString("email", "email notFound");
        String userPic = sharedPreferences.getString("pic", "Pic notFound");




        Log.d("FCM_NOTI", "Token : " + FirebaseInstanceId.getInstance().getToken());
        /**nav**/

        Log.d("mail", "TAKE .." + userMail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            try{
            final String getNo = bundle.getString("Noti");

            //Toast.makeText(this, getNo, Toast.LENGTH_SHORT).show();
            final int notiKey = Integer.parseInt(getNo);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setMessage("Are you did it?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // update to fire store

                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");// for Doc name
                    final String strDate = mdformat.format(calendar.getTime()); // for Doc name
                    final int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                    final int currentYear = calendar.get(Calendar.YEAR);




                    final DocumentReference statisticUD = db.collection("statistic").document(userMail);
                    final DocumentReference plantData = db.collection("plant").document(userMail);
                    final CollectionReference userRef = db.collection("users").document(userMail).collection("notiResult");
                    if (notiKey == 1){
                            //update water
                        /*********get**********/
                        statisticUD.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("Data789", "DocumentSnapshot data: " + document.getData().get("water"));
                                        String getData = (String) document.getData().get("water"); // get data from FS
                                        int dataUD = Integer.parseInt(getData);
                                        Toast.makeText(Notification.this,"Water Data = " + getData,Toast.LENGTH_SHORT).show();
                                        /********Update********/
                                        statisticUD
                                                .update("water", String.valueOf(dataUD + 1))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Result", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(Notification.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Result", "Error updating document", e);
                                                        Toast.makeText(Notification.this, "Failed", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                        plantData
                                                .update("water", String.valueOf(dataUD + 5))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(Notification.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("PlantData", "Error updating document", e);
                                                        Toast.makeText(Notification.this, "Failed", Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(Notification.this,"Light Data = " + getData,Toast.LENGTH_SHORT).show();
                                        /********Update********/
                                        statisticUD
                                                .update("light", String.valueOf(dataUD + 1))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Result", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(Notification.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Result", "Error updating document", e);
                                                        Toast.makeText(Notification.this, "Failed", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                        plantData
                                                .update("light", String.valueOf(dataUD + 5))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(Notification.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("PlantData", "Error updating document", e);
                                                        Toast.makeText(Notification.this, "Failed", Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(Notification.this,"Behavior Data = " + getData,Toast.LENGTH_SHORT).show();
                                        /********Update********/
                                        statisticUD
                                                .update("behavior", String.valueOf(dataUD + 1))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Result", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(Notification.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Result", "Error updating document", e);
                                                        Toast.makeText(Notification.this, "Failed", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                        plantData
                                                .update("fertilizer", String.valueOf(dataUD + 5))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                                        Toast.makeText(Notification.this, "Success", Toast.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("PlantData", "Error updating document", e);
                                                        Toast.makeText(Notification.this, "Failed", Toast.LENGTH_LONG).show();
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




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //add nav
        View navHead = navigationView.getHeaderView(0);
        navUserName = (TextView) navHead.findViewById(R.id.userName) ;
        navUserMail = (TextView) navHead.findViewById(R.id.email);
        navProfilePic = (ImageView) navHead.findViewById(R.id.profile_picture);

        navUserName.setText(userName);
        navUserMail.setText(userMail);
        Picasso.with(Notification.this).load(userPic.toString()).into(navProfilePic);

        /**nav**/

        /**AddButton**/
        addNoti = (Button) findViewById(R.id.addNoti);
        addNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder Builder = new AlertDialog.Builder(Notification.this);
                Builder.setTitle("Set Notification");
                Builder.setMessage("Type your text");
                final EditText input = new EditText(Notification.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                Builder.setView(input);

                Builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sub = input.getText().toString();

                        Calendar calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(Notification.this, Notification.this,
                                year,month,day);



                        datePickerDialog.show();

                    }
                });

                Builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                Builder.show();


            }
        });

        /**AddButton**/
        /**backBtn**/
        backNoti = (Button)findViewById(R.id.notiBack);
        backNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Notification.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        /**Backbtn**/


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
                Intent homeIntent = new Intent(Notification.this,MainActivity.class);
                startActivity(homeIntent);

                break;
            case R.id.nav_notificaion:
                Intent notiIntent = new Intent(Notification.this,Notification.class);
                startActivity(notiIntent);

                break;
            case R.id.nav_statistic:
                Intent statisticIntent = new Intent(Notification.this,Statistic.class);
                startActivity(statisticIntent);

                break;
            case R.id.nav_plant:
                Intent plantIntent = new Intent(Notification.this,Plant.class);
                startActivity(plantIntent);
                finish();
                break;
           /* case R.id.nav_setting:
                Intent settingIntent = new Intent(MainActivity.this,Notification.class);
                startActivity(settingIntent);
                break;
                */
            case R.id.nav_logout:

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                getApplicationContext().getSharedPreferences("userInfo", 0).edit().clear().commit();

                Intent logOut = new Intent(Notification.this,LoginActivity.class);
                startActivity(logOut);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**SetTime**/
    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        mountFinal = i1;
        dayFinal = i2;

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(calendar.HOUR_OF_DAY);
        minute = calendar.get(calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Notification.this, android.app.AlertDialog.THEME_HOLO_LIGHT, Notification.this,
                hour,minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;


        setAlarm(yearFinal,mountFinal,dayFinal,hourFinal,minuteFinal);

        String s = hourFinal +":" +minuteFinal + " " + " | " + sub;
        timeList.add(new notiTyping(
                s
        ));


        recyclerView = (RecyclerView) findViewById(R.id.recyList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        notiAdapter ss = new notiAdapter(timeList);
        recyclerView.setAdapter(ss);

        /*addItem.add(s);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrL);
        addAdapter = new ArrayAdapter<String>(this, android.R.layout.activity_list_item, addItem);
        recyclerView.setAdapter();*/



    }



    private void setAlarm(int yearFinal, int mountFinal, int dayFinal, int hourFinal, int minuteFinal) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //Toast.makeText(MainActivity.this,h + ":" +m, Toast.LENGTH_SHORT).show();
        calendar.set(Calendar.HOUR_OF_DAY, hourFinal);
        calendar.set(Calendar.MINUTE, minuteFinal);
        calendar.set(Calendar.YEAR, yearFinal);
        calendar.set(Calendar.DAY_OF_MONTH, dayFinal);
        calendar.set(Calendar.MONTH, mountFinal);


        Intent intent = new Intent(Notification.this, MyReceiver.class);
        intent.putExtra("userTyping",sub);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

    }

   /* private void addLv() {
        lv1 = (ListView) findViewById(R.id.recyList);
        addItem = new ArrayList<String>();
        addAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, addItem);
        lv1.setAdapter(addAdapter);
    }*/
   /*addLv();
        addItem.add(s);*/
    /**SetTime**/
}
