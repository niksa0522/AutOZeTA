package com.example.autozeta.Basic.UI.termini;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.proto.ProtoOutputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.ChatListRecycleViewAdapter;
import com.example.autozeta.Adapters.TerminiRecyclerViewAdapter;
import com.example.autozeta.Basic.UI.chat.ChatSharedViewModel;
import com.example.autozeta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Contacts;

public class TerminiListFragment extends Fragment {

    private TerminiRecyclerViewAdapter adapter;
    private RecyclerView terminiList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private String uID;
    private List<String> listID,carList,terminList;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_termini_list,container,false);

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        listID=new ArrayList<>();
        carList=new ArrayList<>();
        terminList=new ArrayList<>();

        terminiList = (RecyclerView) Root.findViewById(R.id.termini_list);
        terminiList.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter=new TerminiRecyclerViewAdapter(listID,carList,terminList);
        terminiList.setAdapter(adapter);
        progressBar =(ProgressBar) Root.findViewById(R.id.progressBar);

        DatabaseReference ref = mDatabase.getReference().child("termini").child(uID);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    carList.add(snapshot.child("carInfo").getValue().toString());
                    terminList.add(snapshot.getKey());
                    listID.add(snapshot.child("userID").getValue().toString());
                    adapter.notifyDataSetChanged();
                    terminiList.smoothScrollToPosition(0);
                progressBar.setVisibility(ProgressBar.INVISIBLE);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                carList.remove(snapshot.child("carInfo").getValue().toString());
                terminList.remove(snapshot.getKey());
                listID.remove(snapshot.child("userID").getValue().toString());
                adapter.notifyDataSetChanged();
                terminiList.smoothScrollToPosition(0);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()==progressBar.VISIBLE){
                    Toast.makeText(getContext(),"Ne postoje podaci!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        };
        handler.postDelayed(runnable, 2500);

        return Root;
    }

    @Override
    public void onPause() {

        handler.removeCallbacks(runnable);
        super.onPause();
    }
}
