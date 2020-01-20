package com.chatho.chatho.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chatho.chatho.Adapter.groupmessage_adpt;
import com.chatho.chatho.R;
import com.chatho.chatho.pojo.Group_Messages;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

    private DatabaseReference rootRef;
    private FirebaseAuth auth;
    private RecyclerView rec_chat;
    private EditText input_message;
    private ImageButton send_message,send_files;
    private Toolbar mtoolbar;

    private String messageRecieverID,messageRecieverName,messageRecieverImage,messageSenderID;

    private TextView userName,userLastseen;
    private CircleImageView userImage;

    private final List<Group_Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private groupmessage_adpt adapter;
    private String saveCurrentTime,saveCurrentDate;
    private String checker="",myUri="";
    private StorageTask uploadTask;
    private Uri fileUri;
    private ProgressDialog loading;
    String currentUserId,currentUserName,CurrentDate,CurrentTime,current_userID;
    String groupName;
    String myname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupName = getIntent().getStringExtra("groupname");
        current_userID = getIntent().getStringExtra("current_userID");

        mtoolbar = findViewById(R.id.groupchatbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(groupName);
        send_message = findViewById(R.id.btn_send);

        input_message=findViewById(R.id.input_message);
        send_message=findViewById(R.id.btn_send);
        send_files=findViewById(R.id.btn_send_files);

        rec_chat=findViewById(R.id.rec_chat_group);
        rec_chat.setHasFixedSize(true);
        rec_chat.setLayoutManager(new LinearLayoutManager(this));

        userImage=findViewById(R.id.custom_prof_img);

        auth=FirebaseAuth.getInstance();
        messageSenderID=auth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();





        adapter=new groupmessage_adpt(messagesList,messageSenderID,groupName);

        rec_chat.setAdapter(adapter);


//        Picasso.get().load(messageRecieverImage).resize(100,100).centerInside().placeholder(R.drawable.user_icon).into(userImage);

        send_message.setOnClickListener(view -> {
            sendMessage();
        });



        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMMM dd,yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");

        saveCurrentTime=currentTime.format(calendar.getTime());
        loading=new ProgressDialog(this);

        send_files.setOnClickListener(view -> {

            CharSequence options[] = new CharSequence[]
                    {
                            "Images",
                            "PDF Files",
                            "Ms Word Files"
                    };

            AlertDialog.Builder builder=new AlertDialog.Builder(GroupChatActivity.this);
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

                String messageSendRef="Groups/" +groupName+ messageSenderID + "/" + messageRecieverID;
                String messageRecieveRef="Groups/"+groupName+ messageRecieverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef=rootRef.child("Groups").child(groupName).child(messageSenderID)
                        .child(messageRecieverID).push();

                final String messagePushID=userMessageKeyRef.getKey();

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
                        Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double p=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        loading.setMessage((int) p +"% Uploading...");
                    }
                });

            }else if (checker.equals("image")){

                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");
                String messageSendRef="Messages/" + messageSenderID + "/" + messageRecieverID;
                String messageRecieveRef="Messages/" + messageRecieverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef=rootRef.child("Groups").child(groupName).child(messageSenderID)
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
                                        Log.e("Send message","Message Sent Successfully...");                                    }
                                    else {
                                        loading.dismiss();
                                        Toast.makeText(GroupChatActivity.this, "Error!!", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference my_name=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(messageSenderID);
        my_name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myname=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        DatabaseReference userMessageKeyRef=rootRef.child("Groups").child(groupName).child(messageSenderID)
                .push();

        String messagePushID=userMessageKeyRef.getKey();

        rootRef.child("Groups").child(groupName).child("Messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Group_Messages messages=dataSnapshot.getValue(Group_Messages.class);
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
    }

    private void sendMessage() {

        String messageText=input_message.getText().toString();

        if (TextUtils.isEmpty(messageText)){
            input_message.setError("Enter your message...");
            input_message.findFocus();

        }else
        {
            String messageSendRef="Groups/" +groupName+"/"+"Messages" ;
            //String messageRecieveRef="Groups/" +groupName+ messageRecieverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef=rootRef.child("Groups").child(groupName).child(messageSenderID)
                    .push();

            String messagePushID=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);
            messageTextBody.put("name",myname);



            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSendRef+ "/" + messagePushID,messageTextBody);
           // messageBodyDetails.put(messageRecieveRef+ "/" + messagePushID,messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Log.e("Send message","Message Sent Successfully...");
                    }
                    else {
                        Toast.makeText(GroupChatActivity.this, "Error!!", Toast.LENGTH_SHORT).show();

                    }

                    input_message.setText("");
                }
            });

        }
    }
}
