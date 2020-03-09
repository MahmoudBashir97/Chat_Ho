package com.chatho.chatho.story_package;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chatho.chatho.R;
import com.chatho.chatho.fragments.Status_Fragment;
import com.chatho.chatho.pojo.Stories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class Story_Activity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private DatabaseReference stories_ref;
    private FirebaseAuth auth;
    private String UID="";
    private static final int PROGRESS_COUNT = 6;

    private StoriesProgressView storiesProgressView;
    private ImageView image;

    private String arr[];
    List<Stories> storiesList;
    Stories stories;
    private int counter = 0;
    int count=0,c=-1;
    private final int[] resources = new int[]{
            R.drawable.honda,
            R.drawable.honda,
            R.drawable.honda,
            R.drawable.honda,
            R.drawable.honda,
            R.drawable.honda,
    };

    private final long[] durations = new long[]{
            500L, 1000L, 1500L, 4000L, 5000L, 1000,
    };

    long pressTime = 0L;
    long limit = 500L;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        stories_ref= FirebaseDatabase.getInstance().getReference().child("Stories");
        auth=FirebaseAuth.getInstance();
       // CurrentUID=auth.getCurrentUser().getUid();
        UID=getIntent().getStringExtra("UID");

        storiesList=new ArrayList<>();

        /*
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT); // <- set stories
        storiesProgressView.setStoryDuration(1200L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(); // <- start progress
         */

        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        image = (ImageView) findViewById(R.id.image);

        stories_ref.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    stories=snapshot.getValue(Stories.class);
                    count= Integer.parseInt(snapshot.child("count").getValue().toString());
                    storiesList.add(stories);
                }

                Toast.makeText(Story_Activity.this, ""+count, Toast.LENGTH_SHORT).show();

                storiesProgressView.setStoriesCount(count);
                storiesProgressView.setStoryDuration(4000L);
                // or
                //storiesProgressView.setStoriesCountWithDurations(durations);
                storiesProgressView.setStoriesListener(Story_Activity.this);
                storiesProgressView.startStories();

                arr=new String[100+count];
                for (int i=0;i<count;i++){

                Stories URI=storiesList.get(i);
                arr[i]=URI.getImageURI();
                //image.setImageResource(resources[counter]);
                }

                Picasso.get().load(arr[counter]).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {
        //image.setImageResource(resources[++counter]);
        Picasso.get().load(arr[++counter]).resize(400,720).centerInside().into(image);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
       // image.setImageResource(resources[--counter]);
        Picasso.get().load(arr[--counter]).resize(400,720).centerInside().into(image);
    }
    @Override
    public void onComplete() {
        storiesProgressView.removeAllViews();
        /* Intent i=new Intent(Story_Activity.this, Status_Fragment.class);
        startActivity(i);*/
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}