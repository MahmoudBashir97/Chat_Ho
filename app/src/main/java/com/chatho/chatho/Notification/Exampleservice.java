package com.chatho.chatho.Notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.chatho.chatho.R;


@SuppressLint("Registered")
public class Exampleservice extends Service {

    private NotificationManagerCompat notificationManagerCompat;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private CountDownTimer timer;
    String name,message;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        name=intent.getStringExtra("name");
        message=intent.getStringExtra("message");

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, Notifications.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notification1 = new NotificationCompat.Builder(Exampleservice.this,App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.chat_icon2)
                .setContentTitle(name)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setLights(Color.YELLOW, 500, 500)
                .setSound(alarmSound);
       // notificationManagerCompat.notify(2, notification1);
        NotificationManager nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1,notification1.build());
        startForeground(1, notification1.build());

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    public void Timer(){
        timer=new CountDownTimer(10000,50) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                try{
                    stopSelf();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
            }
        }.start();
    }
}
