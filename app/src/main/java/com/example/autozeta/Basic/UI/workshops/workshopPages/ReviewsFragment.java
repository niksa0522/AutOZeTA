package com.example.autozeta.Basic.UI.workshops.workshopPages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.ReviewsRecycleViewAdapter;
import com.example.autozeta.Adapters.WorkshopsListViewAdapter;
import com.example.autozeta.Basic.UI.workshops.WorkshopsSharedViewModel;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import data.Review;
import data.Workshop;

public class ReviewsFragment extends Fragment {

    private ReviewsRecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private List<Review> reviewsList = new ArrayList<>();
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private WorkshopDataHolder holder;
    private FirebaseAuth mAuth;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_reviews, container, false);

        reviewsList=new ArrayList<>();
        holder = WorkshopDataHolder.getInstance();

        mAuth=FirebaseAuth.getInstance();
        userID= mAuth.getCurrentUser().getUid();

        recyclerView = (RecyclerView) root.findViewById(R.id.reviewsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new ReviewsRecycleViewAdapter(reviewsList);
        recyclerView.setAdapter(adapter);

        db.collection("workshops").document(holder.getWorkshopID()).collection("reviews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        Review review = documentSnapshot.toObject(Review.class);
                        if(documentSnapshot.getId().equals(userID)){
                            List<Review> startingReview = new ArrayList<>();
                            startingReview.add(review);
                            startingReview.addAll(reviewsList);
                            reviewsList.clear();
                            reviewsList.addAll(startingReview);
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(0);
                        }
                        else{
                            reviewsList.add(review);
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }
                }
            }
        });




        return root;
    }
}
