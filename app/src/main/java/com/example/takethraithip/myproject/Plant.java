package com.example.takethraithip.myproject;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class Plant extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    TextView navUserName,navUserMail,waterTxt,ferTxt,lightTxt,testDrag;
    ImageView navProfilePic,treePic,imgFer,imgWater,imgLight;
    Button waterBtn,ferBtn,LightBtn;
    String itemSelect,getWater,getLight,getfer,getWaterN,getLightN,getFerN;
    int water,light,fer,addWater,addFer,addLight;
    StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        testDrag = (TextView) findViewById(R.id.testDrag);

        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String name1 = sharedPreferences.getString("name","not Found");
        String mail1 = sharedPreferences.getString("email","not found");
        String url1 = sharedPreferences.getString("pic","notfound");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        imgFer = (ImageView) findViewById(R.id.imgFer);
        imgLight = (ImageView) findViewById(R.id.imgLight);
        imgWater = (ImageView) findViewById(R.id.imgWater);
        waterTxt = (TextView) findViewById(R.id.warterTxt);
        lightTxt = (TextView) findViewById(R.id.lightTxt);
        ferTxt = (TextView) findViewById(R.id.ferTxt) ;


        treePic = (ImageView) findViewById(R.id.treeSrc);

        String testImgUri = "https://firebasestorage.googleapis.com/v0/b/adroit-terminus-172607.appspot.com/o/giphy.gif?alt=media&token=9fd37d2a-4f57-4715-b1ee-86f2264a69e5";

        Glide.with(this).load(testImgUri).into(treePic);




        /***Nav***/
        View navHead = navigationView.getHeaderView(0);
        navUserName = (TextView) navHead.findViewById(R.id.userName) ;
        navUserMail = (TextView) navHead.findViewById(R.id.email);
        navProfilePic = (ImageView) navHead.findViewById(R.id.profile_picture);



        navUserName.setText(name1);
        navUserMail.setText(mail1);
        Picasso.with(Plant.this).load(url1.toString()).into(navProfilePic);



/****Nav***/

/***get****/
        final DocumentReference plantData = db.collection("plant").document(mail1);

        plantData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("GetStatsssss", "DocumentSnapshot data: " + document.getData());
                        /********data for plant*********/
                        getWater = (String) document.getData().get("water"); // get data from FS
                        waterTxt.setText(getWater);//set Text
                        water = Integer.parseInt(getWater); //used item

                        getLight = (String) document.getData().get("light"); // get data from FS
                        lightTxt.setText(getLight);
                        light = Integer.parseInt(getLight);

                        getfer = (String) document.getData().get("fertilizer"); // get data from FS
                        ferTxt.setText(getfer);
                        fer = Integer.parseInt(getfer);
                        /***********data for plant**********/

                        /**********add data after used item***********/
                        getWaterN = (String) document.getData().get("addWater");
                        addWater = Integer.parseInt(getWaterN);

                        getLightN = (String) document.getData().get("addLight");
                        addLight = Integer.parseInt(getLightN);

                        getFerN = (String) document.getData().get("addFer");
                        addFer = Integer.parseInt(getFerN);
                       /*
                        addWater = (int) document.getData().get("addWater");
                        addFer = (int) document.getData().get("addFer");
                        addLight = (int) document.getData().get("addLight");
                        */


                        /**********add data after used item***********/


                    } else {
                        Log.d("GetStat", "No such document");
                    }
                } else {
                    Log.d("GetStat", "get failed with ", task.getException());
                }
            }
        });


        /********get*******/

        imgLight.setOnLongClickListener(longClickListener);
        imgFer.setOnLongClickListener(longClickListener);
        imgWater.setOnLongClickListener(longClickListener);
        treePic.setOnDragListener(dragListener);


    }//onCreate

    /*******drag*********/
    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {

            ClipData clipData = ClipData.newPlainText("","");
            View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(clipData,myShadowBuilder,view,0);
            }
            else {
                view.startDrag(clipData,myShadowBuilder,view,0);
            }
            return true;
        }
    };

    View.OnDragListener dragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View view, DragEvent event) {

           int dragEvent = event.getAction();

           switch (dragEvent){

               case DragEvent.ACTION_DROP:
                   final View v = (View) event.getLocalState();

                   if (v.getId() == R.id.imgWater){
                       int imgId = 1;
                        updatePlant(imgId);

                       testDrag.setText("Water");
                   }

                   if (v.getId() == R.id.imgFer){
                       int imgId = 2;
                       updatePlant(imgId);

                       testDrag.setText("Fertilzer");
                   }

                   if (v.getId() == R.id.imgLight){

                       int imgId = 3;
                       updatePlant(imgId);

                       testDrag.setText("Light");

                   }

                   break;

           }

           return true;
        }


    };

    private void updatePlant(int imgId) {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String mail1 = sharedPreferences.getString("email","not found");
        final DocumentReference plantData = db.collection("plant").document(mail1);

        switch (imgId) {
            case 1 :
                plantData
                        .update("addWater", String.valueOf(addWater + 1),
                                "water", String.valueOf(water - 1))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                Toast.makeText(Plant.this, "Success", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("PlantData", "Error updating document", e);
                                Toast.makeText(Plant.this, "Failed", Toast.LENGTH_LONG).show();
                            }
                        });

                break;
            case 2 :
                plantData
                        .update("addFer", String.valueOf(addFer + 1),
                                "fertilizer", String.valueOf(fer - 1))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                Toast.makeText(Plant.this, "Success", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("PlantData", "Error updating document", e);
                                Toast.makeText(Plant.this, "Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                break;

            case 3 :
                plantData
                        .update("addLight", String.valueOf(addLight + 1),
                                "light", String.valueOf(light - 1))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("PlantData", "DocumentSnapshot successfully updated!");
                                Toast.makeText(Plant.this, "Success", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("PlantData", "Error updating document", e);
                                Toast.makeText(Plant.this, "Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                break;

        }

        Intent intent = getIntent();
        finish();
        startActivity(intent);



    }

    /********drag*******/




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
                Intent homeIntent = new Intent(Plant.this,MainActivity.class);
                startActivity(homeIntent);
                finish();
                break;
            case R.id.nav_notificaion:
                Intent notiIntent = new Intent(Plant.this,ReminderActivity.class);
                startActivity(notiIntent);
                finish();
                break;
            case R.id.nav_statistic:
                Intent statisticIntent = new Intent(Plant.this,Statistic.class);
                startActivity(statisticIntent);
                finish();
                break;
            case R.id.nav_plant:
                Intent plantIntent = new Intent(Plant.this,Plant.class);
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
