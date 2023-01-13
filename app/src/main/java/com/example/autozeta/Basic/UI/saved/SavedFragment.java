package com.example.autozeta.Basic.UI.saved;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.autozeta.Adapters.WorkshopsListViewAdapter;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import data.Workshop;
import data.WorkshopWithID;

public class SavedFragment extends Fragment {

    FirebaseAuth mAuth;
    private String userID;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    private WorkshopsListViewAdapter adapter;
    private RecyclerView workshopsList;

    private List<Workshop> workshopList = new ArrayList<>();
    private List<String> workshopIdList = new ArrayList<>();
    private List<WorkshopWithID> sortedWorkshops = new ArrayList<>();
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_workshops_list,container,false);

        mAuth=FirebaseAuth.getInstance();
        userID=mAuth.getUid();

        workshopIdList = new ArrayList<>();
        workshopList = new ArrayList<>();
        sortedWorkshops = new ArrayList<>();

        workshopsList = (RecyclerView) Root.findViewById(R.id.workshop_list);
        workshopsList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new WorkshopsListViewAdapter(workshopList,workshopIdList);
        workshopsList.setAdapter(adapter);
        progressBar =(ProgressBar) Root.findViewById(R.id.progressBar);

        mDatabase.getReference().child("saved").child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                db.collection("workshops").document(snapshot.getKey()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        workshopList.add(documentSnapshot.toObject(Workshop.class));
                        workshopIdList.add(documentSnapshot.getId());
                        adapter.notifyDataSetChanged();
                        workshopsList.smoothScrollToPosition(0);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
