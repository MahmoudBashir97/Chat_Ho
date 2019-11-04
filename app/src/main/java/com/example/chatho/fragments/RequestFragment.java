package com.example.chatho.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatho.R;
import com.example.chatho.pojo.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

   private RecyclerView chatRequest_list;
   private DatabaseReference reference,Userref,contacts_ref;
   private FirebaseAuth auth;

   private String CurrentUID;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_request, container, false);


        auth=FirebaseAuth.getInstance();
        CurrentUID=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Chat_Request");
        Userref=FirebaseDatabase.getInstance().getReference().child("Users");
        contacts_ref=FirebaseDatabase.getInstance().getReference().child("Contacts");

        chatRequest_list=v.findViewById(R.id.chatRequest_list);
        chatRequest_list.setLayoutManager(new LinearLayoutManager(getContext()));


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(reference.child(CurrentUID),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestViewHolder holder, int i, @NonNull Contacts model) {


                        holder.accept_btn.setVisibility(View.VISIBLE);
                        holder.cancelt_btn.setVisibility(View.VISIBLE);


                       final String user_list_id=getRef(i).getKey();

                        DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    String type=dataSnapshot.getValue().toString();
                                    if (type.equals("recieved")){

                                        Userref.child(user_list_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final String requestUserImage=dataSnapshot.child("image").getValue().toString();



                                                    Picasso.get().load(requestUserImage).resize(200,200).centerInside().placeholder(R.drawable.user_icon).into(holder.prof_img);

                                                }
                                                final String requestUsername=dataSnapshot.child("name").getValue().toString();
                                                final String requestUserstatus=dataSnapshot.child("status").getValue().toString();

                                                holder.username.setText(requestUsername);
                                                holder.userstatus.setText("wants to connect with you");

                                                holder.itemView.setOnClickListener(view ->
                                                {
                                                    CharSequence options[]=new CharSequence[]
                                                            {"Accept"
                                                            ,
                                                            "Cancel"};
                                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                    builder.setTitle(requestUsername+"Chat Request");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            if (i==0)
                                                            {
                                                                contacts_ref.child(CurrentUID).child(user_list_id)
                                                                        .child("Contact").setValue("Saved")
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                           if (task.isSuccessful()){
                                                                               contacts_ref.child(user_list_id).child(CurrentUID)
                                                                                       .child("Contact").setValue("Saved")
                                                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                           @Override
                                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                                               if (task.isSuccessful()){

                                                                                                   reference.child(CurrentUID).child(user_list_id)
                                                                                                           .removeValue()
                                                                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                               @Override
                                                                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                                                              if (task.isSuccessful()){
                                                                                                                  reference.child(user_list_id).child(CurrentUID)
                                                                                                                          .removeValue()
                                                                                                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                              @Override
                                                                                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                  if (task.isSuccessful()){
                                                                                                                                      Toast.makeText(getContext(), "New Contact added", Toast.LENGTH_SHORT).show();
                                                                                                                                  }
                                                                                                                              }
                                                                                                                          });
                                                                                                              }
                                                                                                               }
                                                                                                           });
                                                                                               }
                                                                                           }
                                                                                       });
                                                                           }
                                                                            }
                                                                        });
                                                            }
                                                            if (i==1)
                                                            {


                                                                reference.child(CurrentUID).child(user_list_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    reference.child(user_list_id).child(CurrentUID)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()){
                                                                                                        Toast.makeText(getContext(), "Contact Deleted!", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                                    builder.create();
                                                    builder.show();
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else if (type.equals("sent")){
                                        Button request_sent_btn=holder.itemView.findViewById(R.id.accept_btn);
                                        request_sent_btn.setText("Req Sent");

                                        holder.itemView.findViewById(R.id.adecline_message_request).setVisibility(View.INVISIBLE);


                                        Userref.child(user_list_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final String requestUserImage=dataSnapshot.child("image").getValue().toString();



                                                    Picasso.get().load(requestUserImage).resize(200,200).centerInside().placeholder(R.drawable.user_icon).into(holder.prof_img);

                                                }
                                                final String requestUsername=dataSnapshot.child("name").getValue().toString();
                                                final String requestUserstatus=dataSnapshot.child("status").getValue().toString();

                                                holder.username.setText(requestUsername);
                                                holder.userstatus.setText("you have sent a request to " + requestUsername);

                                                holder.itemView.setOnClickListener(view ->
                                                {
                                                    CharSequence options[]=new CharSequence[]
                                                            {
                                                                    "Cancel Chat Request"};
                                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                    builder.setTitle("Already Sent Request");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            if (i==0)
                                                            {


                                                                reference.child(CurrentUID).child(user_list_id)
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    reference.child(user_list_id).child(CurrentUID)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()){
                                                                                                        Toast.makeText(getContext(), "you have cancelled the chat request!", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });
                                                    builder.create();
                                                    builder.show();
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                        return new RequestViewHolder(v);
                    }
                };

        chatRequest_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView username , userstatus;
        CircleImageView prof_img;
        Button accept_btn,cancelt_btn;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.user_prof_name);
            userstatus=itemView.findViewById(R.id.user_prof_status);
            accept_btn=itemView.findViewById(R.id.accept_btn);
            cancelt_btn=itemView.findViewById(R.id.cancel_btn);
            prof_img=itemView.findViewById(R.id.users_prof_img);
        }
    }


}
