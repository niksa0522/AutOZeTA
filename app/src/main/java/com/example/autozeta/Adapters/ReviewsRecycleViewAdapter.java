package com.example.autozeta.Adapters;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Basic.UI.workshops.WorkshopActivity;
import com.example.autozeta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

import data.Review;
import data.WorkDaysAndHours;
import data.Workshop;

public class ReviewsRecycleViewAdapter extends RecyclerView.Adapter<ReviewsRecycleViewAdapter.ReviewsViewHolder> {

    private List<Review> listReviews;

    public ReviewsRecycleViewAdapter(List<Review> lr){
        this.listReviews=lr;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);


        return new ReviewsRecycleViewAdapter.ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        holder.reviewName.setText(listReviews.get(position).getUserName());
        holder.reviewRB.setRating(listReviews.get(position).getRating());
        if(listReviews.get(position).getComment()==null || listReviews.get(position).getComment().trim().equals("")){
        }
        else {
            TextView comment = new TextView(holder.itemView.getContext());
            comment.setText(listReviews.get(position).getComment());
            DisplayMetrics metrics = holder.itemView.getContext().getResources().getDisplayMetrics();
            float scale = metrics.density;
            int left,top,bottom;
            left=(int)(20*scale+0.5f);
            top=(int)(10*scale+0.5f);
            bottom=(int)(10*scale+0.5f);
            comment.setPadding(left,top,0,bottom);
            holder.textToAdd.addView(comment);
        }
    }

    @Override
    public int getItemCount() {
        return listReviews.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{
        public TextView reviewName;
        public LinearLayout textToAdd;
        public RatingBar reviewRB;

        public ReviewsViewHolder(View v){
            super(v);
            textToAdd = (LinearLayout) itemView.findViewById(R.id.textToAdd) ;
            reviewName = (TextView) itemView.findViewById(R.id.tvName);
            reviewRB = itemView.findViewById(R.id.rbRating);
        }
    }


}
