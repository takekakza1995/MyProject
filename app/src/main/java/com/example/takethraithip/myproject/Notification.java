package com.example.takethraithip.myproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

public class Notification extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{


    String sub = "";
    Button addNoti;
    int day , month , year , hour , minute;
    int dayFinal , mountFinal , yearFinal , hourFinal, minuteFinal;
    AlarmManager alarmManager;
    PendingIntent alarmIntent;
    private ListView lv1;
    private ArrayAdapter<String> addAdapter;
    private ArrayList<String> addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        /**nav**/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
                Intent homeIntent = new Intent(Notification.this,MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.nav_notificaion:
                Intent notiIntent = new Intent(Notification.this,Notification.class);
                startActivity(notiIntent);
                finish();
                break;
            case R.id.nav_statistic:
                Intent statisticIntent = new Intent(Notification.this,Statistic.class);
                startActivity(statisticIntent);
                finish();
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
            case R.id.nav_logout:

                break;    */

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
        TimePickerDialog timePickerDialog = new TimePickerDialog(Notification.this, Notification.this,
                hour,minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;


        setAlarm(yearFinal,mountFinal,dayFinal,hourFinal,minuteFinal);

        String s = dayFinal +"/"+mountFinal+"/"+ yearFinal + "\n" + "Set Time" + " : " +
                hourFinal+ ":" + minuteFinal + "\n"+
                "Notification" + " : " + sub;

        addLv();
        addItem.add(s);

        addAdapter.notifyDataSetChanged();
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
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

    }

    private void addLv() {
        lv1 = (ListView) findViewById(R.id.notiList);
        addItem = new ArrayList<String>();
        addAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, addItem);
        lv1.setAdapter(addAdapter);
    }
    /**SetTime**/
}
