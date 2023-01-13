package com.example.autozeta.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Basic.UI.termini.TerminActivity;
import com.example.autozeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TerminiRecyclerViewAdapter extends RecyclerView.Adapter<TerminiRecyclerViewAdapter.TerminViewHolder>{

    private List<String> userIDList,carInfoList,terminIDlist;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    public TerminiRecyclerViewAdapter(List<String> userIDList, List<String> carInfoList, List<String> terminIDlist) {
        this.userIDList=userIDList;
        this.carInfoList=carInfoList;
        this.terminIDlist=terminIDlist;
    }

    @Override
    public TerminiRecyclerViewAdapter.TerminViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.termin_list_item, parent, false);
        return new TerminiRecyclerViewAdapter.TerminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TerminiRecyclerViewAdapter.TerminViewHolder holder, int position) {
        final String usersIDs = userIDList.get(position);

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
                    holder.carInfo.setText(carInfoList.get(position));



                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent terminIntent = new Intent(holder.itemView.getContext(), TerminActivity.class);
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

    @Override
    public int getItemCount() {
        return userIDList.size();
    }

    public class TerminViewHolder extends RecyclerView.ViewHolder {

        public TextView carInfo,userName;

        public TerminViewHolder(View view) {
            super(view);
            carInfo = view.findViewById(R.id.tvCarInfo);
            userName = view.findViewById(R.id.tvName);
        }
    }

}
