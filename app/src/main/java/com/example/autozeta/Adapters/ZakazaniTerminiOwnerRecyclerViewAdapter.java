package com.example.autozeta.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Basic.UI.zakazaniTermini.ZakazanTerminActivity;
import com.example.autozeta.Owner.ui.zakazaniTermini.ZakazanTerminOwnerActivity;
import com.example.autozeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import data.ZakazanTermin;

public class ZakazaniTerminiOwnerRecyclerViewAdapter extends RecyclerView.Adapter<ZakazaniTerminiOwnerRecyclerViewAdapter.ZakazaniTerminViewHolder>{
    private List<String> terminIDlist;
    private List<ZakazanTermin> terminiList;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    public ZakazaniTerminiOwnerRecyclerViewAdapter(List<ZakazanTermin> terminiList,List<String> terminIDlist) {
        this.terminiList=terminiList;
        this.terminIDlist=terminIDlist;
    }
    @NonNull
    @Override
    public ZakazaniTerminiOwnerRecyclerViewAdapter.ZakazaniTerminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zakazan_termin_list_item, parent, false);
        return new ZakazaniTerminiOwnerRecyclerViewAdapter.ZakazaniTerminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZakazaniTerminiOwnerRecyclerViewAdapter.ZakazaniTerminViewHolder holder, int position) {
        final String usersIDs = terminiList.get(position).getUserId();

        if(usersIDs==null||usersIDs.equals("")){
            holder.userName.setText(terminiList.get(position).getCarId());
            holder.carInfo.setText(terminiList.get(position).getCar());
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            holder.datum.setText(format.format(terminiList.get(position).getStartDate())+" do "+format.format(terminiList.get(position).getEndDate()));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent terminIntent = new Intent(holder.itemView.getContext(), ZakazanTerminOwnerActivity.class);
                    terminIntent.putExtra("termin_id", terminIDlist.get(position));
                    terminIntent.putExtra("receiver_id", "");
                    terminIntent.putExtra("receiver_name", holder.userName.getText());

                    holder.itemView.getContext().startActivity(terminIntent);
                }
            });
        }
        else{
            mDatabase.getReference().child("users").child(usersIDs).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        final String[] Fname = new String[1];
                        final String retUT = snapshot.child("userType").getValue().toString();
                        if(retUT.equals("Vlasnik")){
                            final String retName = snapshot.child("imeRadionce").getValue().toString();
                            holder.userName.setText(retName);
                        }
                        else{
                            final String retFName = snapshot.child("ime").getValue().toString();
                            final String retLName = snapshot.child("prezime").getValue().toString();
                            final String retName = retFName + " "+ retLName;
                            holder.userName.setText(retName);
                        }
                        holder.carInfo.setText(terminiList.get(position).getCar());
                        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        holder.datum.setText(format.format(terminiList.get(position).getStartDate())+" do "+format.format(terminiList.get(position).getEndDate()));



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent terminIntent = new Intent(holder.itemView.getContext(), ZakazanTerminOwnerActivity.class);
                                terminIntent.putExtra("termin_id",terminIDlist.get(position));
                                terminIntent.putExtra("receiver_id", usersIDs);
                                terminIntent.putExtra("receiver_name", holder.userName.getText());

                                holder.itemView.getContext().startActivity(terminIntent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return terminIDlist.size();
    }

    public class ZakazaniTerminViewHolder extends RecyclerView.ViewHolder {

        public TextView carInfo,userName,datum;

        public ZakazaniTerminViewHolder(View view) {
            super(view);
            datum = view.findViewById(R.id.tvDate);
            carInfo = view.findViewById(R.id.tvCarInfo);
            userName = view.findViewById(R.id.tvName);
        }
    }
}
