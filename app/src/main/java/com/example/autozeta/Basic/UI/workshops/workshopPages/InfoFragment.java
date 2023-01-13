package com.example.autozeta.Basic.UI.workshops.workshopPages;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.autozeta.Basic.UI.chat.ChatActivity;
import com.example.autozeta.Basic.UI.workshops.WorkshopActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Map;

import data.Review;
import data.WorkDaysAndHours;
import data.Workshop;

public class InfoFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");;
    private FirebaseFirestore mFirestore= FirebaseFirestore.getInstance();

    private String workshopID;
    private String userID;
    private Toolbar ChatToolBar;

    private Workshop workshop;

    private Boolean isSaved = false;

    private TextView workPrice,Services,workDays,workshopName,rating,phone;

    private RatingBar rbRating;

    private String userName;

    private ImageView btnCalendar,btnOpenMaps,btnMessage,btnSave;

    private Review review;

    private WorkshopActivity activity;

    private WorkshopDataHolder holder;

    private View root;

    private Boolean started = false;

    private Boolean reviewCheck = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        root =  inflater.inflate(R.layout.fragment_workshop, container, false);
        started=false;
        reviewCheck=false;

        mAuth=FirebaseAuth.getInstance();
        userID= mAuth.getCurrentUser().getUid();

        holder=WorkshopDataHolder.getInstance();

        activity = (WorkshopActivity)getActivity();
        workshopID=activity.workshopID;
        if(holder.getWorkshopID()==null){
        }
        else{
            if(!holder.getWorkshopID().equals(workshopID))
                holder.setNull();
        }
        holder.setWorkshopID(workshopID);

        workshopName=activity.name;
        workshop=holder.getWorkshop();
        if(holder.getReviewID()==null){
            reviewCheck=true;

        }
        else{
            if(!holder.getReviewID().equals(userID)){
                review=null;
                holder.setReviewID(null);
                holder.setReview(null);
                reviewCheck=true;
            }
            else{
                review=holder.getReview();
            }
        }

        InitControllers(root);

        DatabaseReference rootRef = mDatabase.getReference();
        rootRef.child("saved").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(workshopID)){
                    isSaved=true;
                    btnSave.setImageResource(R.drawable.ic_baseline_bookmark_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        if(workshop==null) {
        CollectionReference workshopsRef = mFirestore.collection("workshops");
        workshopsRef.document(workshopID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                workshop = documentSnapshot.toObject(Workshop.class);
                WorkshopSetup(root);
                holder.setWorkshop(workshop);
            }
        });
            workshopsRef.document(workshopID).collection("reviews").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { review = document.toObject(Review.class);

                        SetupHasReview(root,inflater);
                        holder.setReview(review);
                        holder.setReviewID(userID);
                    } else {
                        SetupDosntHaveReview(root,inflater);
                    }
                }
            });
        }
        else{
            WorkshopSetup(root);
            if(reviewCheck)
            {
                CollectionReference workshopsRef = mFirestore.collection("workshops");
                workshopsRef.document(workshopID).collection("reviews").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            review = document.toObject(Review.class);
                            SetupHasReview(root,inflater);
                            holder.setReview(review);
                            holder.setReviewID(userID);
                        } else {
                            SetupDosntHaveReview(root,inflater);
                        }
                    }
                });
            }
            else{
                if(review!=null){
                    SetupHasReview(root,inflater);
                }
                else{
                    SetupDosntHaveReview(root,inflater);
                }
            }



        }

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.viewPager.setCurrentItem(1);
            }
        });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                chatIntent.putExtra("receiver_id", workshopID);
                chatIntent.putExtra("receiver_name", workshop.getName());

                getContext().startActivity(chatIntent);
            }
        });
        btnOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc= String.valueOf("geo:0,0?z=18&q="+workshop.getLocation().getLatitude())+","+String.valueOf(workshop.getLocation().getLongitude());

                Uri gmmIntentUri = Uri.parse(loc+"("+workshop.getName()+")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if(mapIntent.resolveActivity(getActivity().getPackageManager())!=null){
                    startActivity(mapIntent);
                }
                else{
                    Toast.makeText(activity, "Nemate instalirane google mape", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSaved){
                    mDatabase.getReference().child("saved").child(userID).child(workshopID).removeValue();
                    isSaved=false;
                    btnSave.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                }
                else{
                    mDatabase.getReference().child("saved").child(userID).child(workshopID).setValue("");
                    isSaved=true;
                    btnSave.setImageResource(R.drawable.ic_baseline_bookmark_24);
                }
            }
        });


        return root;
    }

    private void InitControllers(View root) {

        workPrice = root.findViewById(R.id.tvCenaRadaValue);
        Services = root.findViewById(R.id.tvServisiList);
        workDays = root.findViewById(R.id.workDaysValues);
        rating = root.findViewById(R.id.tvRating);
        rbRating=root.findViewById(R.id.rbRating);
        phone=root.findViewById(R.id.phoneValues);

        btnCalendar = root.findViewById(R.id.iVCalendar);
        btnOpenMaps =root.findViewById(R.id.iVMaps);
        btnMessage = root.findViewById(R.id.iVContact);
        btnSave = root.findViewById(R.id.iVSave);

    }

    private void WorkshopSetup(View root){
        workPrice.setText(String.valueOf(workshop.getWorkPrice()));
        workshopName.setText(workshop.getName());
        String days="";
        for(WorkDaysAndHours wd:workshop.getWorkDays()){
            if(wd.isOpen())
                days+=wd.getDays()+": "+wd.getStartTime()+"-"+wd.getEndTime()+"\n";
            else
                days+=wd.getDays()+": Ne radi \n";

        }
        days = days.substring(0,days.length()-1);
        String services="";
        for(Map.Entry<String,Boolean> entry:workshop.getServices().entrySet()){
            services+="- "+entry.getKey()+"\n";
        }
        services = services.substring(0,services.length()-1);
        workDays.setText(days);
        Services.setText(services);
        String rat=(workshop.getAvgRating()+"\n"+"("+workshop.getNumRatings()+")");
        rating.setText(rat);
        phone.setText(workshop.getPhoneNum());
        rbRating.setRating((int)workshop.getAvgRating());
    }

    private void SetupHasReview(View root, LayoutInflater inflater){
            ViewGroup main = (ViewGroup) root.findViewById(R.id.viewReview);
            View view = inflater.inflate(R.layout.has_review, null);
            TextView name = (TextView) view.findViewById(R.id.tvName);
            TextView comment = (TextView) view.findViewById(R.id.tvComment);
            RatingBar rb = (RatingBar) view.findViewById(R.id.rbRating);
            ImageView btnEdit = (ImageView) view.findViewById(R.id.btnEdit);

            View.OnClickListener goToReview = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent chatIntent = new Intent(getContext(), ReviewFragment.class);
                    chatIntent.putExtra("Name", review.getUserName());
                    chatIntent.putExtra("Review", review.getRating());
                    chatIntent.putExtra("Comment", review.getComment());

                    getContext().startActivity(chatIntent);
                }
            };

            rb.setRating(review.getRating());
            name.setText(review.getUserName());
            if (review.getComment() == null || review.getComment().equals("")) {
                comment.setText("Dodaj komentar");
                comment.setOnClickListener(goToReview);
            } else {
                comment.setText(review.getComment());
            }

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getContext(),btnEdit);
                    popupMenu.getMenuInflater().inflate(R.menu.review_menu,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.modify:
                                    Intent chatIntent = new Intent(getContext(), ReviewFragment.class);
                                    chatIntent.putExtra("Name", review.getUserName());
                                    chatIntent.putExtra("Review", review.getRating());
                                    chatIntent.putExtra("Comment", review.getComment());

                                    getContext().startActivity(chatIntent);
                                    return true;
                                case R.id.delete:
                                    DeleteReview();
                                    return true;
                                default:
                                    return true;

                            }
                        }
                    });
                    popupMenu.show();
                }
            });

            main.addView(view, 0);
    }

    private void SetupDosntHaveReview(View root, LayoutInflater inflater){
        ViewGroup main = (ViewGroup) root.findViewById(R.id.viewReview);
        View view = inflater.inflate(R.layout.doesnt_have_review, null);
        RatingBar rb = view.findViewById(R.id.rbRating);

        RatingBar.OnRatingBarChangeListener goToReview = new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser) {
                    mDatabase.getReference().child("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final String retFName = snapshot.child("ime").getValue().toString();
                            final String retLName = snapshot.child("prezime").getValue().toString();
                            final String retName = retFName + " " + retLName;


                            Intent chatIntent = new Intent(getContext(), ReviewFragment.class);
                            chatIntent.putExtra("Name", retName);
                            chatIntent.putExtra("Review",(int)ratingBar.getRating());
                            chatIntent.putExtra("Comment","");

                            getContext().startActivity(chatIntent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };

        rb.setOnRatingBarChangeListener(goToReview);

        main.addView(view, 0);
    }


    private void DeleteReview(){

        DocumentReference workshopRef= mFirestore.collection("workshops").document(workshopID);
        DocumentReference ratingsRef = workshopRef.collection("reviews").document(userID);
        mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                    int newNumRatings = workshop.getNumRatings() -1;

                    double oldRatingTotal = workshop.getAvgRating() * workshop.getNumRatings();
                    double newRatingTotal = (oldRatingTotal - review.getRating()) / newNumRatings;

                    workshop.setAvgRating(newRatingTotal);
                    workshop.setNumRatings(newNumRatings);


                    transaction.set(workshopRef, workshop);
                    transaction.delete(ratingsRef);

                holder.setReview(null);
                holder.setReviewID(null);
                review=null;
                holder.setWorkshop(workshop);
                holder.setReviewID(userID);




                return null;
            }
        });
        ViewGroup main = (ViewGroup) root.findViewById(R.id.viewReview);
        LayoutInflater inflater = getLayoutInflater();

        main.removeViewAt(0);
        SetupDosntHaveReview(root, inflater);
        String rat = (workshop.getAvgRating() + "\n" + workshop.getNumRatings());
        rating.setText(rat);
        rbRating.setRating((int) workshop.getAvgRating());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(started) {
            workshop = holder.getWorkshop();
            review = holder.getReview();

            ViewGroup main = (ViewGroup) root.findViewById(R.id.viewReview);
            LayoutInflater inflater = getLayoutInflater();

            main.removeViewAt(0);
            if (review != null) {
                SetupHasReview(root, inflater);
            } else {
                SetupDosntHaveReview(root, inflater);
            }
            String rat = (workshop.getAvgRating() + "\n" + workshop.getNumRatings());
            rating.setText(rat);
            rbRating.setRating((int) workshop.getAvgRating());

        }
        else
            started=true;
    }
}
