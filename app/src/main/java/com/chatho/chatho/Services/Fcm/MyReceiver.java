package com.chatho.chatho.Services.Fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chatho.chatho.fragments.RequestFragment;
import com.chatho.chatho.ui.Chat_Activity;
import com.chatho.chatho.ui.MainActivity;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "MyReceiver";
    private int count=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "onReceive: " + intent.getAction());

                        String senderId = intent.getExtras().getString("senderID");
                        String senderName = intent.getExtras().getString("senderName");
                        String receiverId = intent.getExtras().getString("receiverId");
                        String message = intent.getExtras().getString("message");
                       // String image = intent.getExtras().getString("image");
                        String countbadge= intent.getExtras().getString("countbadge");




                         if (intent.getAction().equals("message")) {
                            context.startActivity(new Intent(context, Chat_Activity.class)
                                    .putExtra("senderId", senderId)
                                    .putExtra("senderName", senderName)
                                    .putExtra("message", message)
                                    //.putExtra("image",image)
                                    .putExtra("countbadge",countbadge)
                                    .addFlags(FLAG_ACTIVITY_NEW_TASK));
                           try {
                                Badges.removeBadge(context);
                                // Alternative way
                                //Badges.setBadge(context, 0);
                            } catch (BadgesNotSupportedException badgesNotSupportedException) {
                                Log.d("Error", badgesNotSupportedException.getMessage());
                            }
                         }
                         if (intent.getAction().equals("chatrequest")) {
                             context.startActivity(new Intent(context, MainActivity.class)
                                     .putExtra("senderId", senderId)
                                     .putExtra("senderName", senderName)
                                     .addFlags(FLAG_ACTIVITY_NEW_TASK));
                         }
    }
}
