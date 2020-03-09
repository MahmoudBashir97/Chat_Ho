package com.chatho.chatho.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chatho.chatho.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sett  extends AppCompatActivity {



    DatabaseReference rf;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageuri;
    private StorageTask uploadtask;

    private Button update_bu;
    private EditText username,status_profile;
    private CircleImageView circleImageView;

    String Current_user_ID;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    private static final int Gallerypick=1;
    private StorageReference UserprofileImage;

    private ProgressDialog loading;
    private Toolbar toolbar;
    private Uri resultUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        update_bu = findViewById(R.id.update_sett);
        username = findViewById(R.id.username);
        status_profile = findViewById(R.id.set_profile_status);
        circleImageView = findViewById(R.id.circle_img);

        toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


        auth = FirebaseAuth.getInstance();
        Current_user_ID = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(Current_user_ID);
        UserprofileImage = FirebaseStorage.getInstance().getReference().child("Profile Images");

        RetrieveUserInfo();
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
        update_bu.setOnClickListener(v ->{
            UpdateSettings();

        });
    }

    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=this.getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadimage(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();
        if (imageuri!=null){
            final StorageReference filereference=UserprofileImage.child(System.currentTimeMillis()+
                    ","+getFileExtension(imageuri));
            uploadtask=filereference.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri downloadUri= (Uri) task.getResult();
                        String muri=downloadUri.toString();

                        reference=FirebaseDatabase.getInstance().getReference("Users").child(Current_user_ID);
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("image",muri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }else {
                        Toast.makeText(Sett.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Sett.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(Sett.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==IMAGE_REQUEST && resultCode== Activity.RESULT_OK
                && data !=null && data.getData() !=null) {
            imageuri = data.getData();

            if (uploadtask != null && uploadtask.isInProgress()) {
                Toast.makeText(Sett.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadimage();


        }
        }

    }

    private void UpdateSettings() {
        String us_name=username.getText().toString();
        String status=status_profile.getText().toString();

        String devicetoken= FirebaseInstanceId.getInstance().getToken();


        if (TextUtils.isEmpty(us_name) || TextUtils.isEmpty(status)){
            username.setError("Enter your username...");
            username.requestFocus();

            status_profile.setError("Enter your status...");
            status_profile.requestFocus();
        }else{
            HashMap<String,Object> profile_Map=new HashMap<>();
            profile_Map.put("Uid",Current_user_ID);
            profile_Map.put("name",us_name);
            profile_Map.put("device_Tokens",devicetoken);
            profile_Map.put("status",status);
            reference.updateChildren(profile_Map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(Sett.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Sett.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    private void SendUserToMainActivity() {

        Intent i=new Intent(Sett.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private void RetrieveUserInfo() {


        reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("image")){
                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus=dataSnapshot.child("status").getValue().toString();
                            String retrieveUserImage=dataSnapshot.child("image").getValue().toString();

                            username.setText(retrieveUserName);
                            status_profile.setText(retrieveUserStatus);
                            Picasso.get().load(retrieveUserImage).resize(200,200).centerInside().placeholder(R.drawable.user_icon).into(circleImageView);
                        }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus=dataSnapshot.child("status").getValue().toString();

                            username.setText(retrieveUserName);
                            status_profile.setText(retrieveUserStatus);

                        }else{
                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(Sett.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
