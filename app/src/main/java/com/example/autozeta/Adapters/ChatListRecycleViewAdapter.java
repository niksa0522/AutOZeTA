package com.example.autozeta.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Basic.UI.chat.ChatActivity;
import com.example.autozeta.Basic.UI.chat.ChatListFragment;
import com.example.autozeta.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

import data.Contacts;

public class ChatListRecycleViewAdapter extends RecyclerView.Adapter<ChatListRecycleViewAdapter.ChatListViewHolder>{


    private List<Contacts> listContacts;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private DatabaseReference UsersRef;

    public ChatListRecycleViewAdapter(List<Contacts> lc){
        this.listContacts=lc;
        if(listContacts!=null)
            SortByDate();
        UsersRef=mDatabase.getReference().child("users");

    }
    private void SortByDate(){
        Collections.sort(listContacts);
    }



    public static class  ChatListViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;
        TextView lastMessage;


        public ChatListViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        final String usersIDs = listContacts.get(position).id;

        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
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
                    String lm = listContacts.get(position).lastMessage;
                    lm = lm.substring(0,Math.min(lm.length(),50));
                    holder.lastMessage.setText(lm);



                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent chatIntent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                            chatIntent.putExtra("receiver_id", usersIDs);
                            chatIntent.putExtra("receiver_name", holder.userName.getText());

                            holder.itemView.getContext().startActivity(chatIntent);
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
        return listContacts.size();
    }
}
