package com.chatho.chatho.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chatho.chatho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register_act extends AppCompatActivity {
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;

    private Button register_bu;
    private EditText email,pass;
    private TextView already_have_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_act);

        register_bu=findViewById(R.id.register_bu);
        email=findViewById(R.id.register_email);
        pass=findViewById(R.id.register_pass);
        already_have_account=findViewById(R.id.already_have_account);
        progressDialog=new ProgressDialog(this);

        auth=FirebaseAuth.getInstance();

        already_have_account.setOnClickListener(v ->{
            SendUserToLoginActivity();
        });
        register_bu.setOnClickListener(v ->{
            CreatenewAccount();
        });

    }

    private void CreatenewAccount() {
        String e_m=email.getText().toString();
        String pa_ss=pass.getText().toString();
        if (TextUtils.isEmpty(e_m) || TextUtils.isEmpty(pa_ss)){
            email.setError("Enter your email...");
            email.requestFocus();

            pass.setError("Enter your password...");
            pass.requestFocus();
        }else {
            progressDialog.setTitle("Creating new account");
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            auth.createUserWithEmailAndPassword(e_m,pa_ss)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){

                           String devicetoken= FirebaseInstanceId.getInstance().getToken();


                           String currentuserID=auth.getCurrentUser().getUid();
                        /*   reference.child("Users").setValue(currentuserID);

                           reference.child("Users").child(currentuserID).child("device_Tokens")
                                   .setValue(devicetoken);*/

                           SendUserToMainActivity();
                           Toast.makeText(Register_act.this, "register success", Toast.LENGTH_SHORT).show();
                           progressDialog.dismiss();
                       }else{
                           Toast.makeText(Register_act.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                           progressDialog.dismiss();
                       }

                        }
                    });
        }
    }

    private void SendUserToLoginActivity() {

        Intent i=new Intent(Register_act.this,Login_act.class);
        startActivity(i);
    }
    private void SendUserToMainActivity() {

        Intent i=new Intent(Register_act.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    private void Check_empty_inputs(){
        String e_m=email.getText().toString();
        String pa_ss=pass.getText().toString();
        if (TextUtils.isEmpty(e_m) || TextUtils.isEmpty(pa_ss)){
            email.setError("Enter your email...");
            email.requestFocus();

            pass.setError("Enter your password...");
            pass.requestFocus();
        }
    }
}
