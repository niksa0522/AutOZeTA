package com.example.autozeta.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Basic.UI.workshops.WorkshopActivity;
import com.example.autozeta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import data.WorkDaysAndHours;
import data.Workshop;

public class WorkshopsListViewAdapter extends RecyclerView.Adapter<WorkshopsListViewAdapter.WorkshopsViewHolder> {


    private List<String> idWorkshops;
    private List<Workshop> listWorkshops;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");


    public WorkshopsListViewAdapter(List<Workshop> lw,List<String> idWorkshops){
        this.listWorkshops=lw;
        this.idWorkshops=idWorkshops;
    }

    public class WorkshopsViewHolder extends RecyclerView.ViewHolder{
        public TextView workshopName, workshopPrice, workshopWorkDays,workshopReview;
        public RatingBar rb;

        public WorkshopsViewHolder(View v){
            super(v);

            workshopName = (TextView) itemView.findViewById(R.id.workshop_name);
            workshopPrice = (TextView) itemView.findViewById(R.id.workshopPriceValue);
            workshopWorkDays = (TextView) itemView.findViewById(R.id.workshopDaysValue);
            workshopReview = (TextView) itemView.findViewById(R.id.workshopStarsNumber);
            rb = itemView.findViewById(R.id.ratingBar);
        }

    }

    @NonNull
    @Override
    public WorkshopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workshop_display_list, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new WorkshopsListViewAdapter.WorkshopsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkshopsViewHolder holder, int position) {

        holder.workshopName.setText(listWorkshops.get(position).getName());
        final double wp = listWorkshops.get(position).getWorkPrice();
        holder.workshopPrice.setText(String.valueOf(wp));
        String workDays="";
        List<WorkDaysAndHours> workdays = listWorkshops.get(position).getWorkDays();
        if(workdays.get(0).isOpen())
            workDays+="Ponedeljak-Petak, ";
        if(workdays.get(1).isOpen())
            workDays+="Subota, ";
        if(workdays.get(2).isOpen())
            workDays+="Nedelja";
        if(workDays.endsWith(", "))
            workDays=workDays.substring(0,workDays.length()-2);
        holder.workshopWorkDays.setText(workDays);

        double rating = listWorkshops.get(position).getAvgRating();
        holder.workshopReview.setText(String.valueOf(rating));
        holder.rb.setRating((float)rating);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(holder.itemView.getContext(), WorkshopActivity.class);
                chatIntent.putExtra("workshop_id", idWorkshops.get(position));

                holder.itemView.getContext().startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listWorkshops.size();
    }

}
