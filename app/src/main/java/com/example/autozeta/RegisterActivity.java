package com.example.autozeta;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autozeta.Basic.HomeBasicActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import data.User;

public class RegisterActivity extends AppCompatActivity {

    EditText emailId,password,fName,lName;
    Button btnSignUp;
    TextView tvSignIn;
    String userType="";
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mFirebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fName=findViewById(R.id.Name);
        lName=findViewById(R.id.Lastname);
        emailId = findViewById(R.id.emailAdr);
        password = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btnReg);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String psw = password.getText().toString();
                String fN = fName.getText().toString();
                String lN = lName.getText().toString();
                if(email.isEmpty())
                {
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                }
                else if(psw.isEmpty())
                {
                    password.setError("Please enter password");
                    password.requestFocus();
                }
                else if(fN.isEmpty())
                {
                    fName.setError("Please enter name");
                    fName.requestFocus();
                }
                else if(lN.isEmpty())
                {
                    lName.setError("Please enter last name");
                    lName.requestFocus();
                }
                else if(email.isEmpty() && psw.isEmpty() && lN.isEmpty() && fN.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Fields are empty",Toast.LENGTH_SHORT).show();
                }
                else if(userType==""){
                    Toast.makeText(RegisterActivity.this,"Choose User Type",Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && psw.isEmpty() && lN.isEmpty() && fN.isEmpty()))
                {
                    FirebaseUser cu = mFirebaseAuth.getCurrentUser();
                    if(cu!=null){
                        mFirebaseAuth.signOut();
                    }
                    mFirebaseAuth.createUserWithEmailAndPassword(email,psw).
                            addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"SignUp Unsuccessful",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        User user = new User(fN,lN,userType);
                                        mFirebaseDB=FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app/");
                                        DatabaseReference mDatabase;
                                        mDatabase=mFirebaseDB.getReference();
                                        mDatabase.child("users").child(userID).setValue(user);
                                        if(userType=="Vlasnik") {
                                            startActivity(new Intent(RegisterActivity.this, WorkshopRegActivity.class));
                                            finish();
                                        }
                                        else {

                                            startActivity(new Intent(RegisterActivity.this, HomeBasicActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()){
            case R.id.rdbtnVR:
                if(checked)
                    userType="Vlasnik";
                break;
            case R.id.rdbtnSK:
                if(checked)
                    userType="Korisnik";
                break;
        }
    }
}