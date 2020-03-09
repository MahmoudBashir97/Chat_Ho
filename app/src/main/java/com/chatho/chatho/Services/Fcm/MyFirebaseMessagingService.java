package com.chatho.chatho.Services.Fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.chatho.chatho.R;
import com.chatho.chatho.ui.MainActivity;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LOG_TAG = "MyFirebaseMessaging";
    public static final String MESSAGE = "message";
    public BubblesManager bubblesManager;
    public NotificationBadge mbadge;
    public ImageView imageView;
    private DatabaseReference getbadgeCount;
    int count=0;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(LOG_TAG, remoteMessage.getData().toString() + "");

        Log.d(LOG_TAG, "onMessageReceived: ");
        String request = remoteMessage.getData().get("request");

        if (remoteMessage.getData().equals("message")){

            String senderId = remoteMessage.getData().get("senderId");
            String senderName = remoteMessage.getData().get("senderName");
            String receiverId=remoteMessage.getData().get("receiverId");
            String message = remoteMessage.getData().get("message");
            String image=remoteMessage.getData().get("imageReceiver");
            String countbadge=remoteMessage.getData().get("countbadge");


            handleInviteIntent(senderId, senderName,receiverId,message,countbadge);

            initBubble();
        }

        if (request.equals("req")){

            String senderId = remoteMessage.getData().get("senderId");
            String senderName = remoteMessage.getData().get("senderName");
            SendRequestChat(senderId,senderName);

        }
    }

    private void handleInviteIntent(String senderId, String senderName,String receiverId, String message,String countbadge) {

      /*  getbadgeCount= FirebaseDatabase.getInstance().getReference().child("countbadge");
                getbadgeCount.child("countbadge").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String d=dataSnapshot.child("countbadge").getValue().toString();
                        count=Integer.parseInt(d);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/


         count= Integer.parseInt(countbadge);

        Intent reciveMessage = new Intent(getApplicationContext(), MyReceiver.class)
                .setAction("message")
                .putExtra("senderId", senderId)
                .putExtra("senderName", senderName)
                .putExtra("receiverId",receiverId)
                .putExtra("message", message)
                //.putExtra("imageReceiver",image)
                .putExtra("countbadge",countbadge);

        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(this, 2, reciveMessage, PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(reciveMessage);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        2,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.app.Notification build = new NotificationCompat.Builder(this, MESSAGE)
                .setSmallIcon(R.drawable.chat_icon2)
                .setPriority(PRIORITY_MAX)
                .setContentTitle(String.format("You have new message from ", senderName))
                .addAction(R.drawable.ic_check_black_24dp, "show message", pendingIntentAccept)
                .setVibrate(new long[3000])
                .setChannelId(MESSAGE)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .build();


        addNewBubble();

        try {

            Badges.setBadge(this,count);
        } catch (BadgesNotSupportedException e) {
            Log.d("Error", e.getMessage());
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(MESSAGE, MESSAGE, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(1, build);

    }

    private void SendRequestChat(String senderId, String senderName){

        Intent reciveMessage = new Intent(getApplicationContext(), MyReceiver.class)
                .setAction("chatrequest")
                .putExtra("senderId", senderId)
                .putExtra("senderName", senderName);

        PendingIntent pendingIntentAccept = PendingIntent.getBroadcast(this, 3, reciveMessage, PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(reciveMessage);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        3,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.app.Notification build = new NotificationCompat.Builder(this, MESSAGE)
                .setSmallIcon(R.drawable.ic_add_request)
                .setPriority(PRIORITY_MAX)
                .setContentTitle(String.format("You have new chat Request \n From", senderName))
                .addAction(R.drawable.ic_check_black_24dp, "Open", pendingIntentAccept)
                .setVibrate(new long[3000])
                .setChannelId(MESSAGE)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(MESSAGE, MESSAGE, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(2, build);

    }

    private void initBubble( ) {
        bubblesManager=new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_remove)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {

                        addNewBubble();
                    }
                }).build();
        bubblesManager.initialize();
    }

    private void addNewBubble() {

        BubbleLayout bubbleView= (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.bubble,null);
        imageView=bubbleView.findViewById(R.id.avatar);

       /// Picasso.get().load(image).resize(50,50).centerInside().placeholder(R.drawable.user_icon).into(imageView);

        mbadge=(NotificationBadge)bubbleView.findViewById(R.id.badge);
        mbadge.setNumber(88);
        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                Toast.makeText(MyFirebaseMessagingService.this, "Removed", Toast.LENGTH_SHORT).show();
            }
        });

        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                Toast.makeText(MyFirebaseMessagingService.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView,60,20);
    }

  /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        bubblesManager.recycle();
    }*/
}
