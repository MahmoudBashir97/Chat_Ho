package com.chatho.chatho.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatho.chatho.Adapter.Recent_story_adpt;
import com.chatho.chatho.R;
import com.chatho.chatho.ViewModel.SharedModel;
import com.chatho.chatho.pojo.Stories;
import com.chatho.chatho.story_package.Story_Activity;
import com.chatho.chatho.story_package.Story_camera;
import com.chatho.chatho.ui.Sett;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Status_Fragment extends Fragment {

    private RelativeLayout Rel_status;
    private Intent i;
    private FloatingActionButton floatingActionButton;
    private Uri fileUri;
    private String myUri="";
    private CircleImageView userimage,plus;
    private TextView tap_to;
    private DatabaseReference stories_ref,story_to_friends,get_users_data;
    private FirebaseAuth auth;
    private String CurrentUID="";
    private String messagePushID="";
    private int count=0;
    private Stories stories;
    private List<Stories> storiesList;
    private boolean add;

    private RecyclerView recyclerView;
    private Recent_story_adpt story_adpt;

    private SharedModel sharedModel;


    String Id,name;
    String randomId,Ranarr[];

    private StorageReference user_Stories;
    private static final int IMAGE_REQUEST=1;
    private Uri imageuri;
    private StorageTask uploadtask;




    public Status_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v=inflater.inflate(R.layout.fragment_status, container, false);
        //i=new Intent("android.media.action.IMAGE_CAPTURE");

        tap_to=v.findViewById(R.id.tap_to);
        userimage=v.findViewById(R.id.userpic);
        plus=v.findViewById(R.id.plus);


        recyclerView=v.findViewById(R.id.friends_status);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // database reference
        stories_ref= FirebaseDatabase.getInstance().getReference().child("Stories");
        story_to_friends=FirebaseDatabase.getInstance().getReference().child("Stories");
        get_users_data= FirebaseDatabase.getInstance().getReference().child("Users");
        user_Stories= FirebaseStorage.getInstance().getReference().child("Stories Images");




        // get current userId
        auth=FirebaseAuth.getInstance();
        CurrentUID=auth.getCurrentUser().getUid();
        messagePushID=stories_ref.getKey();

        get_users_data.child(CurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String img=dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(img).resize(100,80).centerInside().into(userimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Rel_status=v.findViewById(R.id.Rel_status);
        floatingActionButton=v.findViewById(R.id.floatingActionButton);



        //get stories data
        stories_ref.child(CurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    stories=snapshot.getValue(Stories.class);
                }
                if (stories != null){
                String x=stories.getImageURI();
                if (x !=null){
                Picasso.get().load(stories.getImageURI()).resize(100, 80).centerInside().into(userimage);
                    tap_to.setVisibility(View.INVISIBLE);
                    plus.setVisibility(View.INVISIBLE);
                }else {
                    Toast.makeText(getContext(), "There is big missing data", Toast.LENGTH_SHORT).show();
                }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Rel_status.setOnClickListener(view -> {

            stories_ref.child(CurrentUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Stories stories=snapshot.getValue(Stories.class);

                        add=stories.isAdd();

                    }
                    if (add){
                        i=new Intent(getContext(), Story_Activity.class);
                        i.putExtra("UID",CurrentUID);
                        startActivity(i);
                    }
                    else {
                        openImage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        floatingActionButton.setOnClickListener(view ->{
            openImage();
           /* Intent intent=new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent,"Select Image"),438);*/
        });

        stories_ref.child(CurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.child("count").getValue() != null){
                        count=Integer.parseInt(snapshot.child("count").getValue().toString());
                        Toast.makeText(getContext(), ""+count, Toast.LENGTH_SHORT).show();
                        randomId=snapshot.child("randomId").getValue().toString();
                        /*Ranarr=new String[count++];
                        Ranarr[count]=randomId;*/
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //RetreiveRecentData();

        return v;
    }


    public void RetreiveRecentData(){


        storiesList=new ArrayList<>();
        story_to_friends.child("Friends").child(CurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Stories stories=snapshot.getValue(Stories.class);
                    if (stories != null){

                        // Id = stories.getId();
                        if (Id != CurrentUID) {
                            storiesList.add(stories);

                            if (Id !=null){
                            get_users_data.child(Id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        name =dataSnapshot.child("name").getValue().toString();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        }
                    }
                }
                story_adpt=new Recent_story_adpt(getContext(),storiesList,name);
                recyclerView.setAdapter(story_adpt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedModel = ViewModelProviders.of(this).get(SharedModel.class);
        sharedModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String inp) {
                Toast.makeText(getContext(), "mmmmmmm"+inp, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        story_to_friends=FirebaseDatabase.getInstance().getReference().child("Stories");
        get_users_data= FirebaseDatabase.getInstance().getReference().child("Users");


        //RetreiveRecentData();
    }


    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadimage(){
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();
        if (imageuri!=null){
            final StorageReference filereference=user_Stories.child(System.currentTimeMillis()+
                    ","+getFileExtension(imageuri));
            uploadtask=filereference.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                        count=count+1;

                        //stories_ref=FirebaseDatabase.getInstance().getReference("Users").child(CurrentUID);

                        Random random=new Random();
                        int c=random.nextInt(count);
                        //for (int i=0;i<Ranarr.length;i++){
                        //if (Ranarr[i].equals(null)){
                        //if (c != Integer.parseInt(Ranarr[i])){
                        // count=count+1;
                        //stories=new Stories(CurrentUID,""+count,""+imageuri,true,""+c);
                        // }
                        //}
                        // }

                        stories=new Stories(CurrentUID,""+count,""+muri,true,""+count);

                        stories_ref.child(CurrentUID).child(""+count).setValue(stories);
                        story_to_friends.child("Friends").child(CurrentUID).child(""+count).setValue(stories);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==IMAGE_REQUEST && resultCode== Activity.RESULT_OK
                && data !=null && data.getData() !=null) {
            imageuri = data.getData();

            if (uploadtask != null && uploadtask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();

                Picasso.get().load(imageuri).resize(100, 80).centerInside().into(userimage);
                tap_to.setVisibility(View.INVISIBLE);
                plus.setVisibility(View.INVISIBLE);
            } else {
                uploadimage();
            }
        }

       /* if (requestCode==438 && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null){

            fileUri=data.getData();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Story Image Files");





            Random random=new Random();
            int c=random.nextInt(2+count);
            for (int i=0;i<Ranarr.length;i++){
                if (Ranarr[i].equals(null)){
                    if (c != Integer.parseInt(Ranarr[i])){
                        count=count+1;
                        stories=new Stories(CurrentUID,""+count,""+fileUri,true,""+c);
                    }
                }
            }

            count=count+1;
            stories=new Stories(CurrentUID,""+count,""+fileUri,true,""+c);

            stories_ref.child(CurrentUID).child(""+c).setValue(stories);
            story_to_friends.child("Friends").child(CurrentUID).child(""+c).setValue(stories);
            Picasso.get().load(fileUri).resize(100, 80).centerInside().into(userimage);
            tap_to.setVisibility(View.INVISIBLE);
            plus.setVisibility(View.INVISIBLE);

            //int i=1+count;
            //HashMap<String,String> map=new HashMap<>();
            //map.put("ImageURI",""+fileUri);
            //map.put("Count",""+c);


        }*/
    }

}
