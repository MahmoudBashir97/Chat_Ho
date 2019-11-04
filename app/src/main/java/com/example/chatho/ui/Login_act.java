package com.example.chatho.ui;

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

import com.example.chatho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

public class Login_act extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference,userRef;

    private ProgressDialog progressDialog;


    private Button login_bu,phone;
    private EditText email,pass;
    private TextView forget_pass,new_account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_act);

        login_bu=findViewById(R.id.login_bu);
        phone=findViewById(R.id.phone_with);
        email=findViewById(R.id.login_email);
        pass=findViewById(R.id.login_pass);
        forget_pass=findViewById(R.id.forget_pass);
        new_account=findViewById(R.id.new_account);
        auth=FirebaseAuth.getInstance();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");

        reference=FirebaseDatabase.getInstance().getReference();
        currentUser=auth.getCurrentUser();
        progressDialog=new ProgressDialog(this);



        new_account.setOnClickListener(v ->{
            SendUserToRegisterActivity();
        });
        login_bu.setOnClickListener(view -> {
            AllowUserToLogin();
        });

        phone.setOnClickListener(view -> {
            startActivity(new Intent(Login_act.this,PhoneLoginActivity.class));
        });

    }

    private void AllowUserToLogin() {
        String e_m=email.getText().toString();
        String pa_ss=pass.getText().toString();
        if (TextUtils.isEmpty(e_m) || TextUtils.isEmpty(pa_ss)){
            email.setError("Enter your email...");
            email.requestFocus();

            pass.setError("Enter your password...");
            pass.requestFocus();
        }else {
            progressDialog.setTitle("Sign In!");
            progressDialog.setMessage("Please wait");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            auth.signInWithEmailAndPassword(e_m,pa_ss)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){

                           String CurrentUserId=auth.getCurrentUser().getUid();
                           String devicetoken= FirebaseInstanceId.getInstance().getToken();
                           userRef.child(CurrentUserId).child("device_Tokens")
                                   .setValue(devicetoken)
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()){


                                               SendUserToMainActivity();
                                               progressDialog.dismiss();
                                       }
                                       }
                                   });
                       }else {
                           progressDialog.dismiss();
                           Toast.makeText(Login_act.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                       }
                        }
                    });
        }

    }

    @Override
    protected void onStart() {


        super.onStart();

        if (currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {

        Intent i=new Intent(Login_act.this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
    private void SendUserToRegisterActivity() {

        Intent i=new Intent(Login_act.this,Register_act.class);
        startActivity(i);
    }
    private void Check_empty_inputs(){
        String e_m=email.getText().toString();
        String pa_ss=pass.getText().toString();
        if (e_m.isEmpty() || pa_ss.isEmpty()){
            email.setError("Enter your email...");
            email.requestFocus();

            pass.setError("Enter your password...");
            pass.requestFocus();
        }
    }
}
