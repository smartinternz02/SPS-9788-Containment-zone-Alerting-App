package com.saravana.containmentzonealertapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class  App extends Application {
    public static final String LOC_CHANNEL_ID = "location_update";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
       if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
           NotificationChannel locUpdatechannel = new NotificationChannel(
                   LOC_CHANNEL_ID,
                   "Location Update",
                   NotificationManager.IMPORTANCE_DEFAULT
           );
           NotificationManager manager = getSystemService(NotificationManager.class);

           manager.createNotificationChannel(locUpdatechannel);
       }
    }
}
