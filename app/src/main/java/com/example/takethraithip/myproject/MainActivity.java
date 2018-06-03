package com.example.takethraithip.myproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;
    TextView navUserName,navUserMail;
    ImageView navProfilePic,notiView,plantView,statView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                Intent intent = new Intent(MainActivity.this,ReminderActivity.class);
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
/**logout**/





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
                Intent homeIntent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(homeIntent);

                break;
            case R.id.nav_notificaion:
                Intent notiIntent = new Intent(MainActivity.this,ReminderActivity.class);
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
                FirebaseAuth.getInstance().signOut();
                getApplicationContext().getSharedPreferences("userInfo", 0).edit().clear().commit();
                LoginManager.getInstance().logOut();
                Intent logOut = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(logOut);

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
