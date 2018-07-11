package com.example.takethraithip.myproject;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.w3c.dom.Text;

import java.net.URL;
import java.net.URLEncoder;

public class Plant extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    TextView navUserName,navUserMail,waterTxt,ferTxt,lightTxt,testDrag;
    ImageView navProfilePic,treePic,imgFer,imgWater,imgLight,
            fb_share,effect_pic,sun_effect,fer_effect,google_sh,twitt_sh;
    Button waterBtn,ferBtn,LightBtn;

    String itemSelect,getWater,getLight,getfer,getWaterN,getLightN,getFerN;
    int water,light,fer,addWater,addFer,addLight,picCheck;
    StorageReference storageReference;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    private static final int REQ_SELECT_PHOTO = 1;
    private static final int REQ_START_SHARE = 0;

    FirebaseMessaging messaging = FirebaseMessaging.getInstance();
    ShareButton facebook_share_btn;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plant);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


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
        treePic = (ImageView) findViewById(R.id.treeSrc);

        //Glide.with(this).load(R.drawable.tree01).into(treePic);
        loadData();

        imgFer = (ImageView) findViewById(R.id.imgFer);
        imgLight = (ImageView) findViewById(R.id.imgLight);
        imgWater = (ImageView) findViewById(R.id.imgWater);
        waterTxt = (TextView) findViewById(R.id.warterTxt);
        lightTxt = (TextView) findViewById(R.id.lightTxt);
        ferTxt = (TextView) findViewById(R.id.ferTxt) ;
        effect_pic = (ImageView) findViewById(R.id.effectGif) ;
        sun_effect = (ImageView)  findViewById(R.id.sun_effect);
        fer_effect = (ImageView) findViewById(R.id.fer_effect);
        google_sh = (ImageView) findViewById(R.id.google_sh);
        twitt_sh = (ImageView) findViewById(R.id.twitt_sh);



         uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree04");

        twitt_sh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (picCheck == 1){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree01");

                }else if (picCheck == 2){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree02");

                }else if (picCheck ==3){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree03");

                }else if (picCheck == 4){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree04");

                }


                TweetComposer.Builder builder =new TweetComposer.Builder(Plant.this)
                        .text("")
                        .image(uri);
                builder.show();


            }
        });

        //google_sh.setVisibility(View.GONE);
        google_sh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (picCheck == 1){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree01");

                }else if (picCheck == 2){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree02");

                }else if (picCheck ==3){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree03");

                }else if (picCheck == 4){

                    uri = Uri.parse("android.resource://"+getPackageName()+"/drawable/bg_tree04");

                }

                Intent shareIntent = ShareCompat.IntentBuilder
                        .from(Plant.this)
                        .setText("")
                        .setType("image/jpeg").setStream(uri).getIntent()
                        .setPackage("com.example.takethraithip.myproject");

                startActivityForResult(shareIntent,REQ_START_SHARE);

            }
        });





        /***********share*************/
        fb_share = (ImageView) findViewById(R.id.fb_share);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
/*
        facebook_share_btn = (ShareButton) findViewById(R.id.fb_share_fix);

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        if (shareDialog.canShow(SharePhotoContent.class)){
            Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.bg_tree04);
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(image)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            facebook_share_btn.setShareContent(content);

            shareDialog.show(content);

        }*/

/*
                if (shareDialog.canShow(SharePhotoContent.class)){
                    Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.bg_tree04);
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    shareDialog.show(content);
                }*/


        /************/

        fb_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                        Toast.makeText(Plant.this,"Success",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Plant.this,"Cancle",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(Plant.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


              /*  Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext().getResources(),R.drawable.bg_tree04);
                SharePhoto sharePhoto = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();

                if (ShareDialog.canShow(SharePhotoContent.class))
                {
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(sharePhoto)
                            .build();
                    shareDialog.show(content);
                }*/

                if (picCheck == 1){

                    Picasso.with(getBaseContext()).load(R.drawable.bg_tree01).into(target);

                }else if (picCheck == 2){

                    Picasso.with(getBaseContext()).load(R.drawable.bg_tree02).into(target);

                }else if (picCheck ==3){

                    Picasso.with(getBaseContext()).load(R.drawable.bg_tree03).into(target);

                }else if (picCheck == 4){

                    Picasso.with(getBaseContext()).load(R.drawable.bg_tree04).into(target);

                }

            }
        });
        /************share************/


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



        /********get*******/

        imgLight.setOnLongClickListener(longClickListener);
        imgFer.setOnLongClickListener(longClickListener);
        imgWater.setOnLongClickListener(longClickListener);
        treePic.setOnDragListener(dragListener);


    }//onCreate


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

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

                       //Glide.with(Plant.this).load(R.drawable.rain).into(effect_pic);

                       Glide.with(Plant.this).load(R.drawable.rain_v3).into(effect_pic);
                       final Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               effect_pic.setVisibility(View.GONE);
                           }
                       }, 4800);

                        updatePlant(imgId);
                        loadData();



                   }

                   if (v.getId() == R.id.imgFer){
                       int imgId = 2;

                       Glide.with(Plant.this).load(R.drawable.fer_00).into(fer_effect);
                       final Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               fer_effect.setVisibility(View.GONE);
                           }
                       }, 4800);

                       updatePlant(imgId);
                       loadData();

                   }

                   if (v.getId() == R.id.imgLight){

                       int imgId = 3;

                       // Glide.with(Plant.this).load(R.drawable.light_09).into(effect_pic);
                       Glide.with(Plant.this).load(R.drawable.light_10).into(sun_effect);
                       final Handler handler = new Handler();
                       handler.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               sun_effect.setVisibility(View.GONE);
                           }
                       }, 4800);

                       updatePlant(imgId);
                       loadData();

                   }

                   break;

           }

           return true;
        }


    };

    private void loadData() {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String mail1 = sharedPreferences.getString("email","not found");
        final DocumentReference plantData = db.collection("plant").document(mail1);
        final CollectionReference plantDataOff = db.collection("plant");

        plantDataOff.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("PlantOff", "Listen error", e);
                    return;
                }

                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Log.d("PlantOff", String.valueOf(change.getDocument().getData()));
                    }

                    String source = querySnapshot.getMetadata().isFromCache() ?
                            "local cache" : "server";
                    Log.d("PlantOff", "Data fetched from " + source);
                }

            }
        });

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
                        waterTxt.setTextColor(Color.WHITE);
                        water = Integer.parseInt(getWater); //used item

                        getLight = (String) document.getData().get("light"); // get data from FS
                        lightTxt.setText(getLight);
                        light = Integer.parseInt(getLight);

                        getfer = (String) document.getData().get("fertilizer"); // get data from FS
                        ferTxt.setText(getfer);
                        fer = Integer.parseInt(getfer);

                        Log.d("GetFER555", String.valueOf(fer));
                        /***********data for plant**********/

                        /**********add data after used item***********/
                        getWaterN = (String) document.getData().get("addWater");
                        addWater = Integer.parseInt(getWaterN);

                        getLightN = (String) document.getData().get("addLight");
                        addLight = Integer.parseInt(getLightN);

                        getFerN = (String) document.getData().get("addFer");
                        addFer = Integer.parseInt(getFerN);

                        String addW = (String) document.getData().get("addWater");
                        String addF = (String) document.getData().get("addFer");
                        String addL = (String) document.getData().get("addLight");

                        int getAddW = Integer.parseInt(addW);
                        int getAddF = Integer.parseInt(addF);
                        int getAddL = Integer.parseInt(addL);

                        Log.d("TTTTT5555",String.valueOf(getAddW + "||" +getAddF +"||" + getAddL));


                        if (getAddW >= 30  && getAddF >=12 && getAddL >= 12){

                            picCheck = 4;
                            Glide.with(Plant.this).load(R.drawable.tree04).into(treePic);


                        }else if (getAddW >= 20 && getAddF >=8 && getAddL >= 8){

                            picCheck = 3;
                            Glide.with(Plant.this).load(R.drawable.tree03).into(treePic);

                        }else if (getAddW >= 10 && getAddF >=4 && getAddL >= 4){

                            picCheck = 2;
                            Glide.with(Plant.this).load(R.drawable.tree02).into(treePic);

                        }else if (getAddW >= 0 && getAddF >=0 && getAddL >= 0){

                            picCheck = 1;
                            Glide.with(Plant.this).load(R.drawable.tree01).into(treePic);

                        }

                    } else {
                        Log.d("GetStat", "No such document");
                    }
                } else {
                    Log.d("GetStat", "get failed with ", task.getException());
                }
            }
        });



    }

    private void updatePlant(int imgId) {
        sharedPreferences = getSharedPreferences("userInfo",MODE_PRIVATE);
        String mail1 = sharedPreferences.getString("email","not found");
        final DocumentReference plantData = db.collection("plant").document(mail1);

        switch (imgId) {
            case 1 :
                if (water - 1 < 0){
                    Toast.makeText(Plant.this,"Out of Water",Toast.LENGTH_SHORT).show();
                    effect_pic.setVisibility(View.INVISIBLE);
                }else {

                    effect_pic.setVisibility(View.VISIBLE);

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
                }
                break;
            case 2 :
                if (fer - 1 < 0){
                    Toast.makeText(Plant.this,"Out of Fertilizer",Toast.LENGTH_SHORT).show();
                    fer_effect.setVisibility(View.INVISIBLE);
                }else {

                    fer_effect.setVisibility(View.VISIBLE);

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
                }
                break;

            case 3 :
                if (light - 1 < 0){
                    Toast.makeText(Plant.this,"Out of Light",Toast.LENGTH_SHORT).show();
                    sun_effect.setVisibility(View.INVISIBLE);
                }else {

                    sun_effect.setVisibility(View.VISIBLE);

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
                }
                break;

        }

       /* Intent intent = getIntent();
        finish();
        startActivity(intent);*/

        //effect_pic.setVisibility(View.VISIBLE);
        //sun_effect.setVisibility(View.VISIBLE);

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
                Intent notiIntent = new Intent(Plant.this,ReminderActivityV2.class);
                startActivity(notiIntent);
                finish();
                break;
            case R.id.nav_statistic:
                Intent statisticIntent = new Intent(Plant.this,Statistic.class);
                startActivity(statisticIntent);
                finish();
                break;
            case R.id.nav_plant:

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




                Intent logOut = new Intent(Plant.this,LoginActivity.class);
                startActivity(logOut);

                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
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
