package com.example.autozeta.Basic.UI.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.autozeta.LoginAndRegistration.LoginActivity;
import com.example.autozeta.Owner.HomeOwnerActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeBasicFragment extends Fragment {

    private HomeBasicViewModel homeBasicViewModel;
    Button btnLogout;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");;
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeBasicViewModel =
                new ViewModelProvider(this).get(HomeBasicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_basic, container, false);



        root.findViewById(R.id.layoutAuto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_cars);
            }
        });

        root.findViewById(R.id.layoutPoruke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_chat_list);
            }
        });
        root.findViewById(R.id.layoutRadionice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_workshops);
            }
        });
        root.findViewById(R.id.layoutSaved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_saved);
            }
        });
        root.findViewById(R.id.layoutTermin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_termini);
            }
        });
        root.findViewById(R.id.layoutZakazanTermin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(root).navigate(R.id.nav_zakazaniTermini);
            }
        });





        return root;
    }
}
