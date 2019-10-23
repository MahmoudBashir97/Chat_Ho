package com.example.chatho.fragments;


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

import com.example.chatho.R;
import com.example.chatho.pojo.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
   private DatabaseReference reference,Userref;
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
        reference= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        Userref=FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequest_list=v.findViewById(R.id.chatRequest_list);
        chatRequest_list.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRequest_list.hasFixedSize();
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


                        holder.itemView.findViewById(R.id.accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.cancel_btn).setVisibility(View.VISIBLE);


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

                                                    final String requestUsername=dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserstatus=dataSnapshot.child("status").getValue().toString();
                                                    final String requestUserImage=dataSnapshot.child("image").getValue().toString();

                                                    holder.username.setText(requestUsername);
                                                    holder.userstatus.setText(requestUserstatus);

                                                    Picasso.get().load(requestUserImage).placeholder(R.drawable.user_icon).into(holder.prof_img);

                                                }else {
                                                    final String requestUsername=dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserstatus=dataSnapshot.child("status").getValue().toString();

                                                    holder.username.setText(requestUsername);
                                                    holder.userstatus.setText(requestUserstatus);
                                                }
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
