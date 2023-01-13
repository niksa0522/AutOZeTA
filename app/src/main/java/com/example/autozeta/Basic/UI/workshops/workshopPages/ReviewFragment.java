package com.example.autozeta.Basic.UI.workshops.workshopPages;

import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.autozeta.Basic.UI.workshops.WorkshopActivity;
import com.example.autozeta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import data.Review;
import data.Workshop;

public class ReviewFragment extends AppCompatActivity {

    private String name,comment,workshopName,userID,workshopID;
    private int Score;

    private TextView tvName,tvWN;
    private Button post;
    private EditText etComment;
    private RatingBar rb;

    private FirebaseFirestore mFirestore= FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private Workshop workshop;
    private Review review;


    private ImageView cancel;

    private WorkshopDataHolder holder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_review);

        mAuth=FirebaseAuth.getInstance();
        userID= mAuth.getCurrentUser().getUid();

        holder=WorkshopDataHolder.getInstance();

        workshopID = holder.getWorkshopID();
        workshop=holder.getWorkshop();

        workshopName = workshop.getName();
        review=holder.getReview();
        name = getIntent().getExtras().get("Name").toString();
        comment = getIntent().getExtras().get("Comment").toString();
        Score = (int)getIntent().getExtras().get("Review");

        tvWN = findViewById(R.id.nameWorkshop);

        tvName = findViewById(R.id.name);
        rb = findViewById(R.id.rbRating);
        etComment =(EditText) findViewById(R.id.etComment);
        cancel=findViewById(R.id.cancelBtn);
        post=findViewById(R.id.btnPost);

        tvWN.setText(workshopName);


        tvName.setText(name);
        if(comment!=null && !comment.equals("")){
            etComment.setText(comment);
        }
        rb.setRating(Score);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference workshopRef= mFirestore.collection("workshops").document(workshopID);
                DocumentReference ratingsRef = workshopRef.collection("reviews").document(userID);

                mFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                        if(review==null) {
                            int newNumRatings = workshop.getNumRatings() + 1;

                            double oldRatingTotal = workshop.getAvgRating() * workshop.getNumRatings();
                            double newRatingTotal = (oldRatingTotal + rb.getRating()) / newNumRatings;

                            workshop.setAvgRating(newRatingTotal);
                            workshop.setNumRatings(newNumRatings);


                            transaction.set(workshopRef, workshop);
                        }
                        else{
                            double oldRatingTotal = workshop.getAvgRating() * workshop.getNumRatings();
                            double newRatingTotal = (oldRatingTotal -review.getRating() + rb.getRating()) / workshop.getNumRatings();

                            workshop.setAvgRating(newRatingTotal);

                            transaction.set(workshopRef, workshop);
                        }
                        review = new Review(name,etComment.getText().toString(),(int)rb.getRating());
                        transaction.set(ratingsRef,review);

                        holder.setReview(review);
                        holder.setWorkshop(workshop);
                        holder.setReviewID(userID);

                        finish();


                        return null;
                    }
                });
            }
        });




    }


}
