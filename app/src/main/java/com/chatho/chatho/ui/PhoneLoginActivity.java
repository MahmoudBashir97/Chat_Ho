package com.chatho.chatho.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chatho.chatho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button send_verify,Verify;
    private EditText phone_num , input_verify;


    private FirebaseAuth mAuth;
    private String verificationID;
    Intent intent;
    private ProgressBar progressBar;
    Context context;
    DatabaseReference reference;
    FirebaseDatabase database;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        send_verify=findViewById(R.id.send_verify_code);
        Verify=findViewById(R.id.verify_bu);


        phone_num=findViewById(R.id.ph_number);
        input_verify=findViewById(R.id.Verification_code);

        FirebaseApp.initializeApp(context);
        mAuth = FirebaseAuth.getInstance();

        String phone=phone_num.getText().toString();



        send_verify.setOnClickListener(view -> {
            send_verify.setVisibility(View.INVISIBLE);
            phone_num.setVisibility(View.INVISIBLE);


            Verify.setVisibility(View.VISIBLE);
            input_verify.setVisibility(View.VISIBLE);

            String ph_number=phone_num.getText().toString();
            sendVerificationCode(ph_number);
            if (TextUtils.isEmpty(ph_number)){
                Toast.makeText(this, "Phone number is required...", Toast.LENGTH_SHORT).show();
            }else{

            }

        });

        Verify.setOnClickListener(view -> {
            String ccode=input_verify.getText().toString();
            if (ccode.isEmpty() || ccode.length()<6){
                input_verify.setError("أدخل الكود ...");
                input_verify.requestFocus();
                return;
            }
            verifycode(ccode,phone);
        });
    }

    private void verifycode(String code,String phone){
        try { PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationID,code);
            signWithCredential(credential,phone);
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Verification Code is wrong", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }


    private void signWithCredential(PhoneAuthCredential credential, final String phone) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("People").child(userid);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("phone_no", phone);



                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        intent = new Intent(PhoneLoginActivity.this, Settings.class);
                                        intent.putExtra("id",userid);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                }

                            });




                        } else {
                            Toast.makeText(PhoneLoginActivity.this, "You can not register with this email & password !!", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID=s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String codesms=phoneAuthCredential.getSmsCode();
            if (codesms !=null){
                verifycode(codesms,null);
            }


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}
