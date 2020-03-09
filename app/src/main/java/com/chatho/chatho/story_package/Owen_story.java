package com.chatho.chatho.story_package;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatho.chatho.Adapter.Owen_adpt;
import com.chatho.chatho.R;
import com.chatho.chatho.pojo.Stories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Owen_story extends AppCompatActivity {
    private RecyclerView rec_owen_story;
    private Owen_adpt adpt;
    private Stories stories;
    private List<Stories> storiesList;
    private DatabaseReference stories_ref;
    private FirebaseAuth auth;
    private String CurrentUID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owen_story_layout);


        // database reference
        stories_ref= FirebaseDatabase.getInstance().getReference().child("Stories");
        auth= FirebaseAuth.getInstance();
        CurrentUID=auth.getCurrentUser().getUid();

        rec_owen_story=findViewById(R.id.rec_owen_story);
        rec_owen_story.setHasFixedSize(true);
        rec_owen_story.setLayoutManager(new LinearLayoutManager(this));

        stories_ref.child(CurrentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    stories=snapshot.getValue(Stories.class);
                }
                storiesList.add(stories);
                adpt=new Owen_adpt(Owen_story.this,storiesList,CurrentUID);
                rec_owen_story.setAdapter(adpt);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
