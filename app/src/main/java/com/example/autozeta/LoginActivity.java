package com.example.autozeta;

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
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
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


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null){
                    loginUser(mFirebaseUser);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String psw = password.getText().toString();
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
                else if(email.isEmpty() && psw.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Fields are empty",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(LoginActivity.this,"SignIn Unsuccessful",Toast.LENGTH_SHORT).show();
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
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }


    private void loginUser(FirebaseUser mFirebaseUser){
        String userID = mFirebaseUser.getUid();
        DatabaseReference db = mFirebaseDB.getReference().child("users").child(userID);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userType = snapshot.child("userType").getValue().toString();

                if(userType.equals("Korisnik")){
                    Toast.makeText(LoginActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeBasicActivity.class);
                    startActivity(i);
                    mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
                    finish();
                }
                else if(userType.equals("Vlasnik")){
                    Toast.makeText(LoginActivity.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(LoginActivity.this, HomeOwnerActivity.class);
                    startActivity(i);
                    mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}