package com.example.autozeta.Basic.UI.zakazaniTermini;

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
import com.example.autozeta.Adapters.ZakazaniTerminiRecyclerViewAdapter;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.Workshop;
import data.WorkshopWithID;
import data.ZakazanTermin;

public class ZakazaniTerminiListFragment extends Fragment {

    FirebaseAuth mAuth;
    private String userID;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    private ZakazaniTerminiRecyclerViewAdapter adapter;
    private RecyclerView terminiList;

    private List<ZakazanTermin> terminList = new ArrayList<>();
    private List<String> terminiIDList = new ArrayList<>();
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_zakazani_termini_list,container,false);

        mAuth=FirebaseAuth.getInstance();
        userID=mAuth.getUid();

        terminList = new ArrayList<>();
        terminiIDList = new ArrayList<>();
        progressBar =(ProgressBar) Root.findViewById(R.id.progressBar);

        terminiList = (RecyclerView) Root.findViewById(R.id.termini_list);
        terminiList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new ZakazaniTerminiRecyclerViewAdapter(terminList,terminiIDList);
        terminiList.setAdapter(adapter);

        db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                terminList.clear();
                terminiIDList.clear();
                for(QueryDocumentSnapshot snapshot:value){
                    ZakazanTermin termin =  snapshot.toObject(ZakazanTermin.class);
                    terminList.add(termin);
                    terminiIDList.add(snapshot.getId());
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
                terminiList.smoothScrollToPosition(0);
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

        /*db.collection("zakazaniTermini").document(userID).collection("zakazaniTermini").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                for(QueryDocumentSnapshot snapshot:value){
                    ZakazanTermin termin =  snapshot.toObject(ZakazanTermin.class);
                    terminList.add(termin);
                    terminiIDList.add(snapshot.getId());
                }
                adapter.notifyDataSetChanged();
                terminiList.smoothScrollToPosition(0);
                }
            }
        });*/

        return Root;
    }
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
}
