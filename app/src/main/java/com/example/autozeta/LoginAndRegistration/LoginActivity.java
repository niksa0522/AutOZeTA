package com.example.autozeta.LoginAndRegistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autozeta.Basic.HomeBasicActivity;
import com.example.autozeta.Owner.HomeOwnerActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText emailId,password;
    Button btnSignIn;
    TextView tvSignUp,tvForgotPassword;
    FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseDatabase mFirebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDB=FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app/");
        emailId = findViewById(R.id.emailAdr);
        password = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.btnLgn);
        tvSignUp = findViewById(R.id.tvSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null){
                    loginUser(mFirebaseUser);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Prijavite se",Toast.LENGTH_SHORT).show();
                }
            }
        };

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                if(email.isEmpty())
                {
                    emailId.setError("Unesi email");
                    emailId.requestFocus();
                }
                else{
                    mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Email za resetovanje sifre je poslat",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Problem!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String psw = password.getText().toString();
                if(email.isEmpty())
                {
                    emailId.setError("Unesi email");
                    emailId.requestFocus();
                }
                else if(psw.isEmpty())
                {
                    password.setError("Unesi sifru");
                    password.requestFocus();
                }
                else if(email.isEmpty() && psw.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Polja su prazna",Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && psw.isEmpty()))
                {
                    FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    if( mFirebaseUser != null){
                        mFirebaseAuth.signOut();
                    }
                    mFirebaseAuth.signInWithEmailAndPassword(email,psw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Prijavljivanje neuspesno",Toast.LENGTH_SHORT).show();
                            }
                            else{

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
                                loginUser(currentUser);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        if(mFirebaseAuth.getCurrentUser()!=null){
            Intent i = new Intent(LoginActivity.this, HomeOwnerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
            finish();
        }
    }


    private void loginUser(FirebaseUser mFirebaseUser){
        Intent i = new Intent(LoginActivity.this, HomeOwnerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        finish();
    }

}