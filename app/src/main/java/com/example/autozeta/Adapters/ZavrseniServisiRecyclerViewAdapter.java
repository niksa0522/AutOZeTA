package com.example.autozeta.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Basic.UI.zakazaniTermini.ZakazanTerminActivity;
import com.example.autozeta.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import data.ZavrsenServis;

public class ZavrseniServisiRecyclerViewAdapter extends RecyclerView.Adapter<ZavrseniServisiRecyclerViewAdapter.ZavrsenServisViewHolder>{

    private List<ZavrsenServis> servisList;
    private Context context;
    private String userID,carID;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    public ZavrseniServisiRecyclerViewAdapter(List<ZavrsenServis> servisi,Context con,String uid,String cid){
        this.context=con;
        this.userID=uid;
        this.carID=cid;
        this.servisList=servisi;
    }

    @NonNull
    @Override
    public ZavrsenServisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zavrseni_servis_item, parent, false);
        return new ZavrseniServisiRecyclerViewAdapter.ZavrsenServisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZavrseniServisiRecyclerViewAdapter.ZavrsenServisViewHolder holder, int position) {
        ZavrsenServis zavrsenServis = servisList.get(position);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String info = "Na kilometrazi: "+zavrsenServis.getKilometraza()+"km, i datumu: "+format.format(zavrsenServis.getDatum())+",\nRadionica: "+zavrsenServis.getServisName()+"\nTip servisa: " +zavrsenServis.getTipServisa();
        holder.servisInfo.setText(info);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Obrisi Serivs");
                builder.setMessage("Da li zelite da obrisete izabrani servis");
                builder.setPositiveButton("Obrisi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.getReference().child("Cars").child(userID).child(carID).child("servisi").child(servisList.get(position).getServisID()).removeValue();
                    }
                });
                builder.setNegativeButton("Odustani",null);
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return servisList.size();
    }

    public class ZavrsenServisViewHolder extends RecyclerView.ViewHolder {

        public TextView servisInfo;

        public ZavrsenServisViewHolder(View view) {
            super(view);
            servisInfo = view.findViewById(R.id.tvServisInfo);
        }
    }
}
