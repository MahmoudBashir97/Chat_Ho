package com.example.chatho.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatho.R;
import com.example.chatho.fragments.Chats_Fragment;
import com.example.chatho.fragments.Contacts_Fragment;
import com.example.chatho.fragments.Groups_Fragment;
import com.example.chatho.fragments.Tabs_access_adpt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ViewPager mviewPager;
    private TabLayout mtabLayout;
    private Tabs_access_adpt tabs_access_adpt;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtabLayout=findViewById(R.id.tab_lay);
        mviewPager=findViewById(R.id.main_tabs);
        mtoolbar=findViewById(R.id.inc_app_bar);

        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chat ho!");
        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();

        tabs_access_adpt=new Tabs_access_adpt(getSupportFragmentManager());
        tabs_access_adpt.addFragment(new Chats_Fragment(),"Chats");
        tabs_access_adpt.addFragment(new Groups_Fragment(),"Groups");
        tabs_access_adpt.addFragment(new Contacts_Fragment(),"Contacts");

        mviewPager.setAdapter(tabs_access_adpt);
        mtabLayout.setupWithViewPager(mviewPager);
    }

    @Override
    protected void onStart() {


        super.onStart();

        if (currentUser == null){
            SendUserLoginActivity();
        }else {
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance() {
        String currentuserID=auth.getCurrentUser().getUid();
        reference.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           if (dataSnapshot.child("name").exists()){
               Log.d("Welcome","Welcome!");
           }    else {SendUserToSettingsActivity(); }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserLoginActivity() {
        Intent i=new Intent(this,Login_act.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();

    }
    private void SendUserToSettingsActivity() {
        Intent i=new Intent(MainActivity.this,Settings.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId() == R.id.logout){
             auth.signOut();
             SendUserLoginActivity();
         }
        if (item.getItemId() == R.id.settings){
            startActivity(new Intent(MainActivity.this,Settings.class));
        }
        if (item.getItemId() == R.id.find_friends){
            startActivity(new Intent(MainActivity.this,FindFriendsActivity.class));
        }
        if (item.getItemId() == R.id.create_group){
            RequestNewgroup();

        }
        return true;
    }

    private void RequestNewgroup() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.AlertDialog);
        builder.setTitle("Enter Froup Name :");

        final EditText groupname=new EditText(this);
        groupname.setHint("e.g Chat ho");
        builder.setView(groupname);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName=groupname.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "please write Group Name...", Toast.LENGTH_SHORT).show();
                }else {
                    CreateNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              dialogInterface.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroup(String groupName) {
        reference.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, groupName+" group is Created Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
