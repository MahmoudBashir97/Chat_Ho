package com.chatho.chatho.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chatho.chatho.R;
import com.chatho.chatho.ui.GroupChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class Groups_Fragment extends Fragment {

    private ListView grouplist;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listof_groups=new ArrayList<>();
    private String CurrentUser;
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public Groups_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_groups, container, false);

        auth=FirebaseAuth.getInstance();
        CurrentUser=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Groups");

        grouplist=v.findViewById(R.id.group_list);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,listof_groups);
        grouplist.setAdapter(arrayAdapter);

        RetrieveAndDisplayGroups();
        grouplist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String currenyGroupName=adapterView.getItemAtPosition(i).toString();
                Intent groupintent=new Intent(getContext(), GroupChatActivity.class);
                groupintent.putExtra("groupname",currenyGroupName);
                groupintent.putExtra("current_userID",CurrentUser);
                startActivity(groupintent);
            }
        });

        return v;
    }

    private void RetrieveAndDisplayGroups() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                listof_groups.clear();
                listof_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
