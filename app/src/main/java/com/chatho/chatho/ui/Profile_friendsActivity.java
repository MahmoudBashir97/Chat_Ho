package com.chatho.chatho.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chatho.chatho.Api_Interface.api_Interface;
import com.chatho.chatho.Model.data;
import com.chatho.chatho.Model.send;
import com.chatho.chatho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile_friendsActivity extends AppCompatActivity {

    CircleImageView userprofileImage;
    TextView username, userStatus;
    Button SendMaessage,cancelmessagerequest;

    DatabaseReference reference, chatReq,Contacts_ref,notificationRef,getname;
    FirebaseAuth auth;
    String reciever_user_id, Current_state, sender_UserId,device_Tokens,SenderName;
    String BaseURL="https://fcm.googleapis.com/";
    send storeReq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);


        reciever_user_id = getIntent().getStringExtra("visit_user_id");
        device_Tokens=getIntent().getStringExtra("device_Tokens");

        username = findViewById(R.id.visit_username);
        userStatus = findViewById(R.id.user_status);
        SendMaessage = findViewById(R.id.send_message_user);
        cancelmessagerequest = findViewById(R.id.adecline_message_request);

        userprofileImage = findViewById(R.id.visit_prof_img);
        Current_state = "new";
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(reciever_user_id);
        chatReq = FirebaseDatabase.getInstance().getReference().child("Chat_Request");
        Contacts_ref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
        getname=FirebaseDatabase.getInstance().getReference().child("Users");


        sender_UserId = auth.getCurrentUser().getUid();
        Retreive_user_Info();
    }

    private void Retreive_user_Info() {


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {

                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String user_Status = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).resize(200,200).centerInside().placeholder(R.drawable.user_icon).into(userprofileImage);
                    username.setText(userName);
                    userStatus.setText(user_Status);

                    MessageChatRequeast();
                } else {

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String user_Status = dataSnapshot.child("status").getValue().toString();

                    username.setText(userName);
                    userStatus.setText(user_Status);
                    MessageChatRequeast();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MessageChatRequeast() {

        chatReq.child(sender_UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(reciever_user_id)){
                    String requestType=dataSnapshot.child(reciever_user_id).child("request_type").getValue().toString();
                    if (requestType.equals("sent")){
                        Current_state="request_sent";
                        SendMaessage.setText("Cancel Chat Request");
                    }else if (requestType.equals("recieved")){
                        Current_state="request_recieved";
                        SendMaessage.setText("Accept Chat Request");
                        cancelmessagerequest.setVisibility(View.VISIBLE);
                        cancelmessagerequest.setEnabled(true);
                        cancelmessagerequest.setOnClickListener(view -> {
                            CancelChatRequest();
                        });
                    }
                }else {
                    Contacts_ref.child(sender_UserId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(reciever_user_id)){
                                        Current_state="friends";
                                        SendMaessage.setText("Remove this Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!sender_UserId.equals(reciever_user_id)) {

            SendMaessage.setOnClickListener(view -> {
                SendMaessage.setEnabled(false);
                if (Current_state.equals("new")) {
                    SendChatRequest();
                }
                if (Current_state.equals("request_sent")){
                    CancelChatRequest();
                }
                if (Current_state.equals("request_recieved")){
                    AcceptChatRequest();
                }
                if (Current_state.equals("friends")){
                    RemoveSpecificContact();
                }


            });
        } else {
            SendMaessage.setVisibility(View.INVISIBLE);

        }

    }
    private void RemoveSpecificContact(){
        Contacts_ref.child(sender_UserId).child(reciever_user_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            Contacts_ref.child(reciever_user_id).child(sender_UserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                SendMaessage.setEnabled(true);
                                                Current_state="new";
                                                SendMaessage.setText("Send Message");

                                                cancelmessagerequest.setVisibility(View.INVISIBLE);
                                                cancelmessagerequest.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest(){

        Contacts_ref.child(sender_UserId).child(reciever_user_id)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Contacts_ref.child(reciever_user_id).child(sender_UserId)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                chatReq.child(sender_UserId).child(reciever_user_id)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    chatReq.child(reciever_user_id).child(sender_UserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                   SendMaessage.setEnabled(true);
                                                                                   Current_state="friends";
                                                                                   SendMaessage.setText("Remove this Contact");

                                                                                   cancelmessagerequest.setVisibility(View.INVISIBLE);
                                                                                   cancelmessagerequest.setEnabled(false);

                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });

                                            }

                                        }
                                    });

                        }

                    }
                });
    }
    private void CancelChatRequest() {

        chatReq.child(sender_UserId).child(reciever_user_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            chatReq.child(reciever_user_id).child(sender_UserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                SendMaessage.setEnabled(true);
                                                Current_state="new";
                                                SendMaessage.setText("Send Message");

                                                cancelmessagerequest.setVisibility(View.INVISIBLE);
                                                cancelmessagerequest.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {
        getname.child(sender_UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                SenderName=dataSnapshot.child("name").toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        data chatRequest=new data(sender_UserId,SenderName,"req");
        storeReq=new send(device_Tokens,chatRequest);
        chatReq.child(sender_UserId).child(reciever_user_id).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            chatReq.child(reciever_user_id).child(sender_UserId)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                HashMap<String,String> chatNotif=new HashMap<>();
                                                chatNotif.put("from",sender_UserId);
                                                chatNotif.put("type","request");

                                                notificationRef.child(reciever_user_id).push()
                                                        .setValue(chatNotif)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){


                                                                    SendMaessage.setEnabled(true);
                                                                    Current_state="request_sent";
                                                                    SendMaessage.setText("Cancel Chat Request");

                                                                    Retrofit retrofit=new Retrofit.Builder()
                                                                            .baseUrl(BaseURL)
                                                                            .addConverterFactory(GsonConverterFactory.create())
                                                                            .build();

                                                                    api_Interface iterfaceCall=retrofit.create(api_Interface.class);
                                                                    Call<send> sendCall=iterfaceCall.chatRequest(storeReq);
                                                                    sendCall.enqueue(new Callback<send>() {
                                                                        @Override
                                                                        public void onResponse(Call<send> call, Response<send> response) {
                                                                            send sendResponse = response.body();
                                                                            Log.e("send", "sendResponse --> " + sendResponse);
                                                                        }
                                                                        @Override
                                                                        public void onFailure(Call<send> call, Throwable t) {
                                                                            t.getMessage();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });



                                                SendMaessage.setEnabled(false);
                                                Current_state="request_recieved";
                                                SendMaessage.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }else
                        {
                            SendMaessage.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
}