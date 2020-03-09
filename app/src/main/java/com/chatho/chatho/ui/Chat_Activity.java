package com.chatho.chatho.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.chatho.chatho.Adapter.MessageAdapter;
import com.chatho.chatho.Api_Interface.api_Interface;
import com.chatho.chatho.Notification.Exampleservice;
import com.chatho.chatho.R;
import com.chatho.chatho.Services.MySingleton;
import com.chatho.chatho.pojo.Messages;
import com.chatho.chatho.pojo.data;
import com.chatho.chatho.pojo.notifymodel;
import com.chatho.chatho.pojo.send;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Chat_Activity extends AppCompatActivity {

    private DatabaseReference rootRef,NotifRef,getname,reference,get_ReceiverToken,Receiver_Data,countbadgeRef,rff;
    private FirebaseAuth auth;
    private RecyclerView rec_chat;
    private EditText input_message;
    private ImageButton send_message,send_files;
    private Toolbar chat_toolbar;

    private String messageRecieverID,messageRecieverName,messageRecieverImage,messageSenderID;

    private TextView userName,userLastseen;
    private CircleImageView userImage;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter adapter;
    private String saveCurrentTime,saveCurrentDate;
    private String checker="",myUri="";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loading;
    String myname;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAWgnVk88:APA91bFtPXAiE9tx8V_SmqGvxj36-sLpniex0SpoacQvejdBDVRSLFvK_NOH2bKV-H9pB6H3QZkzCbylCX-B-CWgxTj5dWRst6uhB8Fi7GZI1xXFAtfs_RyMfOY-1zHmHDnRlQ0vBAzP";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private String CurrentUserID;
    int countbadge=0;



    String BaseURL="https://fcm.googleapis.com/";
    String user_Token;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chat_toolbar=findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBrView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBrView);


        input_message=findViewById(R.id.input_message);
        send_message=findViewById(R.id.btn_send);
        send_files=findViewById(R.id.btn_send_files);

        rec_chat=findViewById(R.id.rec_chat);
        rec_chat.setHasFixedSize(true);
        rec_chat.setLayoutManager(new LinearLayoutManager(this));

        userName=findViewById(R.id.custom_prof_name);
        userLastseen=findViewById(R.id.custom_lastseen);
        userImage=findViewById(R.id.custom_prof_img);

        auth=FirebaseAuth.getInstance();
        messageSenderID=auth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        NotifRef= FirebaseDatabase.getInstance().getReference();
        getname=FirebaseDatabase.getInstance().getReference().child("Users");
        get_ReceiverToken=FirebaseDatabase.getInstance().getReference().child("Users");
        reference= FirebaseDatabase.getInstance().getReference();
        Receiver_Data=FirebaseDatabase.getInstance().getReference().child("Receiver_Data");
        countbadgeRef=FirebaseDatabase.getInstance().getReference().child("countbadge");


        messageRecieverID=getIntent().getStringExtra("visit_user_id");
        messageRecieverName=getIntent().getStringExtra("visit_user_name");
        messageRecieverImage=getIntent().getStringExtra("visit_image");


        actionBrView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(Chat_Activity.this,Profile_friendsActivity.class);
                i.putExtra("visit_user_id",messageRecieverID);
                startActivity(i);
            }
        });


        adapter=new MessageAdapter(messagesList,messageRecieverID);

        rec_chat.setAdapter(adapter);


        userName.setText(messageRecieverName);
        Picasso.get().load(messageRecieverImage).resize(100,100).centerInside().placeholder(R.drawable.user_icon).into(userImage);
        DisplayLastseen();
        send_message.setOnClickListener(view -> {
            sendMessage();
        });


        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");

        saveCurrentTime=currentTime.format(calendar.getTime());
        loading=new ProgressDialog(this);




        DisplayLastseen();


        FirebaseUser currentUser=auth.getCurrentUser();

        if (currentUser !=null){
            updateUserStatue("online");
        }



        send_files.setOnClickListener(view -> {

            CharSequence options[] = new CharSequence[]
                    {
                            "Images",
                            "PDF Files",
                            "Ms Word Files"
                    };

            AlertDialog.Builder builder=new AlertDialog.Builder(Chat_Activity.this);
            builder.setTitle("Select the File");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (i ==0){
                        checker="image";

                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                    }
                    if (i ==1){
                        checker="pdf";

                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent.createChooser(intent,"Select PDF File"),438);

                    }
                    if (i ==2){
                        checker="docx";

                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/msword");
                        startActivityForResult(intent.createChooser(intent,"Select MS Word File"),438);
                    }
                }
            });
            builder.create();
            builder.show();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if (requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null){


           loading.setTitle("Sending File");
           loading.setMessage("Please wait, we are sending that file...");
           loading.setCanceledOnTouchOutside(false);
           loading.show();

           fileUri=data.getData();

        if (!checker.equals("image")){
            StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document Files");

            String messageSendRef="Messages/" + messageSenderID + "/" + messageRecieverID;
            String messageRecieveRef="Messages/" + messageRecieverID + "/" + messageSenderID;

            rootRef.child("Messages").child(messageSenderID)
                    .child(messageRecieverID).push();

            final String messagePushID=rootRef.getKey();

            final StorageReference filepath=storageReference.child(messagePushID+ "." + checker);
            filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){


                        Map messageTextBody=new HashMap();
                        messageTextBody.put("message",task.getResult().getUploadSessionUri().getLastPathSegment().toString());
                        messageTextBody.put("name",fileUri.getLastPathSegment());
                        messageTextBody.put("type",checker);
                        messageTextBody.put("from",messageSenderID);

                        messageTextBody.put("to",messageRecieverID);
                        messageTextBody.put("messageID",messagePushID);
                        messageTextBody.put("time",saveCurrentTime);
                        messageTextBody.put("date",saveCurrentDate);



                        Map messageBodyDetails=new HashMap();
                        messageBodyDetails.put(messageSendRef+ "/" + messagePushID,messageTextBody);
                        messageBodyDetails.put(messageRecieveRef+ "/" + messagePushID,messageTextBody);

                        rootRef.updateChildren(messageTextBody);
                        loading.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loading.dismiss();
                    Toast.makeText(Chat_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    loading.setMessage((int) p +"% Uploading...");
                }
            });

        }
        else if (checker.equals("image")){

            StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");
            String messageSendRef="Messages/" + messageSenderID + "/" + messageRecieverID;
            String messageRecieveRef="Messages/" + messageRecieverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef=rootRef.child("Messages").child(messageSenderID)
                    .child(messageRecieverID).push();

            String messagePushID=userMessageKeyRef.getKey();

            StorageReference filepath=storageReference.child(messagePushID+ "."+"jpg");
            uploadTask=filepath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloaduri=task.getResult();
                        myUri=downloaduri.toString();


                        Map messageTextBody=new HashMap();
                        messageTextBody.put("message",myUri);
                        messageTextBody.put("name",fileUri.getLastPathSegment());
                        messageTextBody.put("type",checker);
                        messageTextBody.put("from",messageSenderID);

                        messageTextBody.put("to",messageRecieverID);
                        messageTextBody.put("messageID",messagePushID);
                        messageTextBody.put("time",saveCurrentTime);
                        messageTextBody.put("date",saveCurrentDate);



                        Map messageBodyDetails=new HashMap();
                        messageBodyDetails.put(messageSendRef+ "/" + messagePushID,messageTextBody);
                        messageBodyDetails.put(messageRecieveRef+ "/" + messagePushID,messageTextBody);

                        rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){

                                    loading.dismiss();
                                    Toast.makeText(Chat_Activity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    loading.dismiss();
                                    Toast.makeText(Chat_Activity.this, "Error!!", Toast.LENGTH_SHORT).show();

                                }

                                input_message.setText("");
                            }
                        });

                    }
                }
            });

        }else {
            loading.dismiss();
            Toast.makeText(this, "Nothing Selectd,Error.", Toast.LENGTH_SHORT).show();
        }

        }
    }

    private void DisplayLastseen(){


        Receiver_Data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                messageRecieverID=dataSnapshot.child("visit_user_id").getValue().toString();
                messageRecieverName=dataSnapshot.child("visit_user_name").getValue().toString();
                messageRecieverImage=dataSnapshot.child("visit_image").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        rootRef.child("Users").child(messageRecieverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("userState").hasChild("state")){
                            String state=dataSnapshot.child("userState").child("state").getValue().toString();
                            String date=dataSnapshot.child("userState").child("date").getValue().toString();
                            String time=dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online")){
                                userLastseen.setText("online");
                            }else if (state.equals("offline")){
                                userLastseen.setText("Last Seen: " + date +" "+time);
                            }
                        }else{
                                userLastseen.setText("offline");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=auth.getCurrentUser();

        if (currentUser !=null){
            updateUserStatue("online");
        }else{DisplayLastseen();}


        getname.child(messageSenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myname=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rootRef.child("Messages").child(messageSenderID).child(messageRecieverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages=dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        adapter.notifyDataSetChanged();
                        rec_chat.smoothScrollToPosition(rec_chat.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        get_ReceiverToken.child(messageRecieverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        user_Token=dataSnapshot.child("device_Tokens").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void sendMessage() {
       // FirebaseMessaging.getInstance().subscribeToTopic("Send_Message");

        Log.e("token",""+FirebaseInstanceId.getInstance().getToken());

        String messageText=input_message.getText().toString();

        get_ReceiverToken.child(messageRecieverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        user_Token=dataSnapshot.child("device_Tokens").getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


       // Toast.makeText(this, "name : "+data_send.getData().getSenderName()+" id : "+data_send.getData().getSenderId()+" message : "+data_send.getData().getMessage(), Toast.LENGTH_SHORT).show();


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api_Interface iterfaceCall=retrofit.create(api_Interface.class);


        if (TextUtils.isEmpty(messageText)){
            input_message.setError("Enter your message...");
            input_message.findFocus();

        }else
        {
            countbadge++;

            data stored=new data(messageSenderID,myname,messageRecieverID,messageText,messageRecieverImage ,""+countbadge);
            send data_send=new send(user_Token,stored);
            Call<send> sendCall=iterfaceCall.storedata(data_send);
            sendCall.enqueue(new Callback<send>() {
                @Override
                public void onResponse(Call<send> call, retrofit2.Response<send> response) {

                    send sendResponse = response.body();
                    Log.e("send", "sendResponse --> " + sendResponse);
                    // Toast.makeText(Chat_Activity.this, "sent"+sendResponse.getTo(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<send> call, Throwable t) {
                    Toast.makeText(Chat_Activity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            String messageSendRef="Messages/" + messageSenderID + "/" + messageRecieverID;
            String messageRecieveRef="Messages/" + messageRecieverID + "/" + messageSenderID;



            DatabaseReference userMessageKeyRef=rootRef.child("Messages").child(messageSenderID)
                              .child(messageRecieverID).push();

            String messagePushID=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            messageTextBody.put("to",messageRecieverID);
            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);
            messageTextBody.put("name",myname);


            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSendRef+ "/" + messagePushID,messageTextBody);
            messageBodyDetails.put(messageRecieveRef+ "/" + messagePushID,messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Log.e("Send message","Message Sent Successfully...");                    }
                    else {
                        Toast.makeText(Chat_Activity.this, "Error!!", Toast.LENGTH_SHORT).show();

                    }

                    input_message.setText("");
                }
            });

            DatatoFirebaseTobeNotified(messageText);
            notifymodel Notify=new notifymodel(myname,messageText,messageRecieverID,messageSenderID);

        }
    }

    private void DataToNotification(String Message,String Name) {


        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", Name);
            notifcationBody.put("message", Message);

            notification.put("to", messageRecieverID);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);

    }

    private void DatatoFirebaseTobeNotified(String Message){

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
        .child("Notif");
        HashMap<String,String> data=new HashMap<>();
        data.put("to",messageRecieverID);
        data.put("name",myname);
        data.put("message",Message);

        ref.child(messageRecieverID).push().setValue(data);

    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Chat_Activity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    public void setNotification(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Notif");
        ref.child(messageRecieverID);
    }

    public void startService(String messageText) {
        Intent serviceIntent = new Intent(this, Exampleservice.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        serviceIntent.putExtra("name",myname);
        serviceIntent.putExtra("message",messageText);
        ContextCompat.startForegroundService(this, serviceIntent);
        startService(serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, Exampleservice.class);
        stopService(serviceIntent);
    }

    private void updateUserStatue(String state){

        String saveCurrentTime,saveCurrentDate;

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");

        saveCurrentTime=currentTime.format(calendar.getTime());

        HashMap<String,Object> onlinestate=new HashMap<>();
        onlinestate.put("time",saveCurrentTime);
        onlinestate.put("date",saveCurrentDate);
        onlinestate.put("state",state);

        CurrentUserID=auth.getCurrentUser().getUid();

        reference.child("Users").child(CurrentUserID).child("userState")
                .updateChildren(onlinestate);
    }

    private void countbadgeMethod(){



        countbadgeRef.child(messageSenderID).child(messageRecieverID);
        HashMap<String,String> map=new HashMap<>();
        map.put("countbadge",""+countbadge);

        countbadgeRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Chat_Activity.this, "Counted", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}


