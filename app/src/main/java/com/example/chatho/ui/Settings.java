package com.example.chatho.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private Button update_bu;
    private EditText username,status_profile;
    private CircleImageView circleImageView;

    String Current_user_ID;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    private static final int Gallerypick=1;
    private StorageReference UserprofileImage;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        update_bu=findViewById(R.id.update_sett);
        username=findViewById(R.id.username);
        status_profile=findViewById(R.id.set_profile_status);
        circleImageView=findViewById(R.id.circle_img);

        auth=FirebaseAuth.getInstance();
        Current_user_ID=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference();
        UserprofileImage= FirebaseStorage.getInstance().getReference().child("Profile Images");

        loading=new ProgressDialog(this);

        username.setVisibility(View.INVISIBLE);

        circleImageView.setOnClickListener(v ->{

            Intent gallery=new Intent();
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult(gallery,Gallerypick);


        });
        update_bu.setOnClickListener(v ->{
            UpdateSettings();

        });

        RetrieveUserInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallerypick && resultCode==RESULT_OK && data!=null){
            Uri imageuri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);



            if (requestCode==RESULT_OK){


                loading.setTitle("Set Profile Image");
                loading.setMessage("Please wait, your profile image is updating...");
                loading.setCanceledOnTouchOutside(false);
                loading.show();


                Uri resultUri=result.getUri();
                StorageReference filepath =UserprofileImage.child(Current_user_ID + ".jpg");

                filepath.putFile(resultUri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Settings.this, "Profile Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            //Task<Uri> result = task.getStorage().getDownloadUrl();
                            final String downloadUri=task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            reference.child("Users").child(Current_user_ID).child("image")
                                    .setValue(downloadUri)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(Settings.this, "Image Saved in database...", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }else {
                                        Toast.makeText(Settings.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Settings.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }

                    }
                });
            }


        }
    }

    private void RetrieveUserInfo() {

        reference.child("Users").child(Current_user_ID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists() && dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("image")){
                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus=dataSnapshot.child("status").getValue().toString();
                            String retrieveUserImage=dataSnapshot.child("image").getValue().toString();

                            username.setText(retrieveUserName);
                            status_profile.setText(retrieveUserStatus);
                            Picasso.get().load(retrieveUserImage).into(circleImageView);
                        }else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveUserStatus=dataSnapshot.child("status").getValue().toString();

                            username.setText(retrieveUserName);
                            status_profile.setText(retrieveUserStatus);

                        }else{
                            username.setVisibility(View.VISIBLE);
                            Toast.makeText(Settings.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void UpdateSettings() {
        String us_name=username.getText().toString();
        String status=status_profile.getText().toString();

        if (TextUtils.isEmpty(us_name) || TextUtils.isEmpty(status)){
            username.setError("Enter your username...");
            username.requestFocus();

            status_profile.setError("Enter your status...");
            status_profile.requestFocus();
        }else{
            HashMap<String,String> profile_Map=new HashMap<>();
            profile_Map.put("Uid",Current_user_ID);
            profile_Map.put("name",us_name);
            profile_Map.put("status",status);
            reference.child("Users").child(Current_user_ID).setValue(profile_Map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(Settings.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Settings.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
    private void SendUserToMainActivity() {

        Intent i=new Intent(Settings.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
