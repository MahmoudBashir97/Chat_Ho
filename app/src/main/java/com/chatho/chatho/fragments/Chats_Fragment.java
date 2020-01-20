package com.chatho.chatho.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chatho.chatho.R;
import com.chatho.chatho.pojo.Contacts;
import com.chatho.chatho.ui.Chat_Activity;
import com.chatho.chatho.ui.FindFriendsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chats_Fragment extends Fragment {

    private DatabaseReference chat,User_ref,Receiver_Data;
    private FirebaseAuth auth;
    private RecyclerView rec_chat_list;
    private EditText input_message;
    private ImageButton send_message;
    private Toolbar chat_toolbar;
    private String UID;
    private FloatingActionButton newchtfr;
    Context context;

    public Chats_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_chats, container, false);

        auth=FirebaseAuth.getInstance();
        UID=auth.getCurrentUser().getUid();
        chat= FirebaseDatabase.getInstance().getReference().child("Contacts").child(UID);
        User_ref= FirebaseDatabase.getInstance().getReference().child("Users");
        Receiver_Data=FirebaseDatabase.getInstance().getReference().child("Receiver_Data");
        newchtfr=v.findViewById(R.id.newchtfr);

        newchtfr.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                             startActivity(new Intent(getContext(), FindFriendsActivity.class));

                                        }
                                    });


        rec_chat_list=v.findViewById(R.id.chats_list);
        rec_chat_list.setHasFixedSize(true);
        rec_chat_list.setLayoutManager(new LinearLayoutManager(getContext()));



        return v;
    }

    @Override
    public void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chat,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,chatsHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, chatsHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull chatsHolder chatsHolder, int i, @NonNull Contacts contacts) {
                        final String userIDs=getRef(i).getKey();
                        final String[] retImag = {"default_image"};

                        User_ref.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           if (dataSnapshot.exists()){
                               if (dataSnapshot.hasChild("image")){
                                   chatsHolder.count_mess.setVisibility(View.VISIBLE);
                                   retImag[0] =dataSnapshot.child("image").getValue().toString();
                                   Picasso.get().load(retImag[0]).resize(200,200).centerInside().into(chatsHolder.prof_img);
                               }
                                   final String retName=dataSnapshot.child("name").getValue().toString();
                                   final String retStatus=dataSnapshot.child("status").getValue().toString();
                                   chatsHolder.username.setText(retName);

                                   chatsHolder.status.setText("Last Seen: " + "\n"+" Date "+" Time ");

                               if (dataSnapshot.child("userState").hasChild("state")){
                                   String state=dataSnapshot.child("userState").child("state").getValue().toString();
                                   String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                   String time=dataSnapshot.child("userState").child("time").getValue().toString();

                                   if (state.equals("online")){
                                       chatsHolder.status.setText("online");
                                   }else if (state.equals("offline")){
                                       chatsHolder.status.setText("Last Seen: " + date +" "+time);
                                   }
                               }else{
                                   chatsHolder.status.setText("offline");

                               }


                               chatsHolder.itemView.setOnClickListener(v ->{
                                   Intent chatintent=new Intent(getContext(), Chat_Activity.class);
                                   chatintent.putExtra("visit_user_id",userIDs);
                                   chatintent.putExtra("visit_user_name",retName);
                                   chatintent.putExtra("visit_image", retImag[0]);

                                   HashMap<String,String> Receiver_Data_map=new HashMap<>();

                                   Receiver_Data_map.put("visit_user_id",userIDs);
                                   Receiver_Data_map.put("visit_user_name",retName);
                                   Receiver_Data_map.put("visit_image", retImag[0]);

                                   Receiver_Data.setValue(Receiver_Data_map)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()){
                                               Log.e("Receiver Data","success");
                                           }
                                       }
                                   });
                                   startActivity(chatintent);
                               });

                           }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public chatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                       View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                        return new chatsHolder(v);
                    }
                };

        rec_chat_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class chatsHolder extends RecyclerView.ViewHolder{

        CircleImageView prof_img;
        TextView username,status,count_mess;


        public chatsHolder(@NonNull View itemView) {

            super(itemView);

            prof_img=itemView.findViewById(R.id.users_prof_img);
            username=itemView.findViewById(R.id.user_prof_name);
            status=itemView.findViewById(R.id.user_prof_status);
            count_mess=itemView.findViewById(R.id.count_mess);
        }
    }
}
