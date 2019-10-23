package com.example.chatho.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatho.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageButton sendMessage;
    private EditText userMessageInput;
    private ScrollView scrollView;
    private TextView displayTextMessage;

    private DatabaseReference reference,groupnameRef,GroupMessageKeyRef;
    private FirebaseAuth auth;

    String currentUserId,currentUserName,CurrentDate,CurrentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        String groupName=getIntent().getStringExtra("groupname");

        mtoolbar=findViewById(R.id.groupchatbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(groupName);
        sendMessage=findViewById(R.id.btn_send);
        userMessageInput=findViewById(R.id.typing_message);
        displayTextMessage=findViewById(R.id.text_display);

        scrollView=findViewById(R.id.myscroll);

        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName);

        GetUserInfo();

        sendMessage.setOnClickListener(view -> {
            saveMessageInfoToDatabse();

            userMessageInput.setText("");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        groupnameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
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

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatName=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String) ((DataSnapshot)iterator.next()).getValue();

            if (currentUserId.equals(firebaseUser.getUid())){
                displayTextMessage.setBackgroundResource(R.drawable.background_right);
            }else{
                displayTextMessage.setBackgroundResource(R.drawable.background_left);
            }

            displayTextMessage.append(chatName+" :\n"+chatMessage+" :\n"+chatTime+"      "+chatDate+"\n\n\n");


            scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }


    private void GetUserInfo() {

        reference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName=dataSnapshot.child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveMessageInfoToDatabse() {
    String message=userMessageInput.getText().toString();
    String messageKey=groupnameRef.push().getKey();
    if (TextUtils.isEmpty(message)){
        Toast.makeText(this, "please write message first...", Toast.LENGTH_SHORT).show();
    }else {
        Calendar ccalForDate= Calendar.getInstance();
        SimpleDateFormat currentDateFormate= new SimpleDateFormat("MMM dd, yyyy");
        CurrentDate=currentDateFormate.format(ccalForDate.getTime());

        Calendar ccalForTime= Calendar.getInstance();
        SimpleDateFormat currentTimeFormate= new SimpleDateFormat("hh:mm a");
        CurrentTime=currentTimeFormate.format(ccalForTime.getTime());


        HashMap<String,Object> groupMessageKey=new HashMap<>();
        groupnameRef.updateChildren(groupMessageKey);


        GroupMessageKeyRef=groupnameRef.child(messageKey);
        HashMap<String,Object> messageInfoMap=new HashMap<>();
        messageInfoMap.put("name",currentUserName);
        messageInfoMap.put("message",message);
        messageInfoMap.put("date",CurrentDate);
        messageInfoMap.put("time",CurrentTime);
        GroupMessageKeyRef.updateChildren(messageInfoMap);


    }

    }

}
