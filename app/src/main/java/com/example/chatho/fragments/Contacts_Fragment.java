package com.example.chatho.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class Contacts_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference ContRef,UserRef;
    private FirebaseAuth auth;
    private String currentUserId;
    public Contacts_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_contacts, container, false);

        recyclerView=v.findViewById(R.id.contacts_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        ContRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,contactsviewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, contactsviewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull contactsviewHolder holder, int i, @NonNull Contacts contacts) {
                        String userIDs=getRef(i).getKey();

                        UserRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")){
                                    String proImage=dataSnapshot.child("image").getValue().toString();
                                    String proStatus=dataSnapshot.child("status").getValue().toString();
                                    String username=dataSnapshot.child("name").getValue().toString();

                                    holder.userName.setText(username);
                                    holder.userStatus.setText(proStatus);

                                    Picasso.get().load(proImage).placeholder(R.drawable.user_icon).into(holder.prof_img);
                                }else {
                                    String proImage=dataSnapshot.child("image").getValue().toString();
                                    String proStatus=dataSnapshot.child("status").getValue().toString();
                                    String username=dataSnapshot.child("name").getValue().toString();

                                    holder.userName.setText(username);
                                    holder.userStatus.setText(proStatus);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public contactsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display,parent,false);
                        return  new contactsviewHolder(v);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class contactsviewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircleImageView prof_img;
        public contactsviewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_prof_name);
            userStatus=itemView.findViewById(R.id.user_prof_status);
            prof_img=itemView.findViewById(R.id.users_prof_img);
        }
    }
}
