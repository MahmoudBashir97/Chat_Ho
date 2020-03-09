package com.chatho.chatho.Notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;


@SuppressLint("Registered")
public class Notifications extends AppCompatActivity {
    private NotificationManagerCompat notificationManagerCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.notification_lay);


                    notificationManagerCompat=NotificationManagerCompat.from(this);


                   SharedPreferences prefs = getSharedPreferences("com.example.srushtee.dummy", MODE_PRIVATE);

                    Intent serviceIntent=new Intent(Notifications.this,Exampleservice.class);
                    serviceIntent.putExtra("inputExtra","Start service");
                    startService(serviceIntent);
                }
    public void startService(View v){
        SharedPreferences.Editor editor = getSharedPreferences("com.example.srushtee.dummy", MODE_PRIVATE).edit();
        editor.putBoolean("active",true);
        editor.commit();
        editor.apply();
        Intent serviceIntent=new Intent(this,Exampleservice.class);
        serviceIntent.putExtra("inputExtra","checked");
        startService(serviceIntent);
    }

    public void stopService(View v){

        SharedPreferences.Editor editor = getSharedPreferences("com.example.srushtee.dummy", MODE_PRIVATE).edit();
        editor.putBoolean("stop",false);
        editor.apply();
        editor.commit();
        Intent serviceIntent=new Intent(this,Exampleservice.class);
        startService(serviceIntent);
    }
}
