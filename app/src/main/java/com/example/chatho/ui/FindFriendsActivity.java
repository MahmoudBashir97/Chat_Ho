package com.example.chatho.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatho.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        recyclerView=findViewById(R.id.rec_list_findfriends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ref= FirebaseDatabase.getInstance().getReference().child("Users");

        toolbar=findViewById(R.id.find_friends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ref,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int i, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.usetstatus.setText(model.getStatus());

                Picasso.get().load(model.getImage()).placeholder(R.drawable.user_icon).into(holder.prof_img);

                holder.itemView.setOnClickListener(view -> {

                    String visit_user_id=getRef(i).getKey();

                    Intent intent=new Intent(FindFriendsActivity.this,Profile_friendsActivity.class);
                    intent.putExtra("visit_user_id",visit_user_id);
                    startActivity(intent);


                });


            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                FindFriendsViewHolder viewHolder=new FindFriendsViewHolder(v);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView userName,usetstatus;
        CircleImageView prof_img;
        ImageView online_img;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_prof_name);
            usetstatus=itemView.findViewById(R.id.user_prof_status);
            prof_img=itemView.findViewById(R.id.users_prof_img);
            online_img=itemView.findViewById(R.id.user_online);
        }
    }
}
