package com.example.autozeta.Adapters;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import data.Message;
import data.Termin;
import de.hdodenhof.circleimageview.CircleImageView;

public class TerminRecyclerViewAdapter extends RecyclerView.Adapter<TerminRecyclerViewAdapter.TerminViewHolder> {


    private List<Termin> messageList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    public TerminRecyclerViewAdapter (List<Termin> userMessagesList)
    {
        this.messageList = userMessagesList;
    }

    @NonNull
    @Override
    public TerminRecyclerViewAdapter.TerminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.termin_item, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new TerminRecyclerViewAdapter.TerminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TerminRecyclerViewAdapter.TerminViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Termin termin = messageList.get(position);

        String fromUserID = termin.getFromUserId();

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);

        String date;

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        date= (format.format(termin.getStartDate()));

        String text="";
        text+=termin.getCar()+"\n";
        text+="Tip usluge: "+termin.getServiceType()+"\n";
        text+=date+"\n";
        if(termin.getTimeNeeded()!=0)
            text+="Vreme rada "+ termin.getTimeNeeded()+"\n";
        if(termin.getPrice()!=0)
            text+="Ukupna cena (delovi + cena rada): "+ termin.getPrice();
        if(termin.getMessage()!=null && !termin.getMessage().equals(""))
            text+="\nPoruka: " + termin.getMessage();


            if(fromUserID.equals(messageSenderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setText(text);


            }
            else{
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(text);
            }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class TerminViewHolder extends  RecyclerView.ViewHolder{
        public TextView senderMessageText, receiverMessageText;

        public TerminViewHolder(View v){
            super(v);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
        }

    }
}
