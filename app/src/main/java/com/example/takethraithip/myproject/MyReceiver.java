package com.example.takethraithip.myproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Take Thraithip on 3/1/2018.
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "Wake Up!", Toast.LENGTH_LONG).show();
        Intent intent1 = new Intent(context, UserNoti.class);
        //context.startActivity(intent1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent1 , PendingIntent.FLAG_CANCEL_CURRENT);

      /*  int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String channelId= "user_notification";
        String channelName = "Take Notification";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,channelName,importance);
            NotificationManager manager = (NotificationManager).getSystemService(Context.NOTIFICATION_SERVICE);
        }

        */


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(),"")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Notification")
                .setContentText("พักสายตาเถอะนะคนดี!!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
