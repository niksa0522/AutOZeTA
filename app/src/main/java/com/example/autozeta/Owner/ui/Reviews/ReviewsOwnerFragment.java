package com.example.autozeta.Owner.ui.Reviews;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.ReviewsRecycleViewAdapter;
import com.example.autozeta.Basic.UI.workshops.workshopPages.WorkshopDataHolder;
import com.example.autozeta.Owner.HomeOwnerActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.Review;
import data.Workshop;

public class ReviewsOwnerFragment extends Fragment {

    private ReviewsRecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<Review> reviewsList = new ArrayList<>();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String userID;
    private Workshop workshop;
    private TextView ocena,BrojOcena;
    private RatingBar rbOcena;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reviews_owner, container, false);

        reviewsList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        workshop = ((HomeOwnerActivity) getActivity()).workshop;

        if (workshop == null) {
            db.collection("workshops").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    workshop = documentSnapshot.toObject(Workshop.class);
                    InitOcena(root);
                }
            });
        }
        else{
            InitOcena(root);
        }


        recyclerView = (RecyclerView) root.findViewById(R.id.reviewsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ReviewsRecycleViewAdapter(reviewsList);
        recyclerView.setAdapter(adapter);
        progressBar =(ProgressBar) root.findViewById(R.id.progressBar);

        db.collection("workshops").document(userID).collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Review review = documentSnapshot.toObject(Review.class);
                        reviewsList.add(review);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(0);
                    }
                }
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

        return root;
    }

    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void InitOcena(View root) {
        ocena=root.findViewById(R.id.ocena);
        BrojOcena=root.findViewById(R.id.broj_ocena);
        rbOcena=root.findViewById(R.id.rbOcena);
        ocena.setText(String.valueOf(workshop.getAvgRating()));
        BrojOcena.setText(String.valueOf(workshop.getNumRatings()));
        rbOcena.setRating((float)workshop.getAvgRating());
    }
}
