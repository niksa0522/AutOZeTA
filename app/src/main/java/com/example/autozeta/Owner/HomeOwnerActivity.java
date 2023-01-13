package com.example.autozeta.Owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.autozeta.ActivityCheckClass;
import com.example.autozeta.Basic.UI.chat.ChatActivity;
import com.example.autozeta.LoginAndRegistration.LoginActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import data.Workshop;

public class HomeOwnerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public Workshop workshop=null;
    public NavigationView navigationView;
    private String userID;
    private TextView logout,userName;
    private String username;
    private FirebaseFirestore mFirestore= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(firebaseAuth.getCurrentUser()==null){
            Intent intToMain = new Intent(this, LoginActivity.class);
            startActivity(intToMain);
            finish();
        }
        else {
            userID = firebaseAuth.getUid();
            mDatabase.getReference().child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        final String[] Fname = new String[1];
                        final String retUT = snapshot.child("userType").getValue().toString();
                        if (retUT.equals("Vlasnik")) {
                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                            username = retName;
                            SetupOwner();
                        } else {
                            final String retFName = snapshot.child("ime").getValue().toString();
                            final String retLName = snapshot.child("prezime").getValue().toString();
                            final String retName = retFName + " " + retLName;
                            username = retName;
                            SetupStandard();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {

                            if (!task.isSuccessful())
                                return;

                            // Get new FCM registration token
                            String token = task.getResult();
                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser != null) {
                                mDatabase.getReference().child("Tokens").child(firebaseUser.getUid()).setValue(token);
                                FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid());
                            }
                        }
                    });
        }
    }

    private void SetupOwner(){

        setContentView(R.layout.activity_home_owner);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chat_list, R.id.nav_termini,R.id.nav_zakazaniTermini,R.id.nav_modify,R.id.nav_calendar_owner,R.id.nav_ocene)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        mFirestore.collection("workshops").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                workshop = documentSnapshot.toObject(Workshop.class);
            }
        });
        logout = findViewById(R.id.logout);
        userName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        userName.setText(username);
        SetupLogout(logout);
    }
    private void SetupLogout(TextView tv){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {

                                if(!task.isSuccessful())
                                    return;

                                // Get new FCM registration token
                                String token = task.getResult();
                                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                if(firebaseUser!=null){
                                    mDatabase.getReference().child("Tokens").child(firebaseUser.getUid()).removeValue();
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(firebaseUser.getUid());
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intToMain = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intToMain);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
    private void SetupStandard(){
        setContentView(R.layout.activity_home_basic);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_chat_list,R.id.nav_workshops,R.id.nav_saved,R.id.nav_cars,R.id.nav_termini,R.id.nav_zakazaniTermini)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        logout = findViewById(R.id.logout);
        userName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        userName.setText(username);
        SetupLogout(logout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()==null){
            Intent intToMain = new Intent(this, LoginActivity.class);
            startActivity(intToMain);
            finish();
        }
    }

    @Override
    protected void onPause() {
        ActivityCheckClass.setOtherUser(null);
        ActivityCheckClass.ClearActivity(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        ActivityCheckClass.setOtherUser(null);
        ActivityCheckClass.SetActivity(this);
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}