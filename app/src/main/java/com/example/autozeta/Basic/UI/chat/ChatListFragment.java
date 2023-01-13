package com.example.autozeta.Basic.UI.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.Adapters.ChatListRecycleViewAdapter;
import com.example.autozeta.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Contacts;
import data.Message;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListFragment extends Fragment {

    private ChatListRecycleViewAdapter adapter;
    // private FirebaseRecyclerAdapter<String,ChatListViewHolder> adapter;
    private RecyclerView chatsList;
    private FirebaseAuth mAuth;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private String uID;
    private ChatSharedViewModel SharedViewModel;
    private List<Contacts> listContacts = new ArrayList<>();;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View Root = inflater.inflate(R.layout.fragment_chat_list,container,false);

        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        ChatsRef =mDatabase.getReference().child("Contacts").child(uID);
        UsersRef=mDatabase.getReference().child("users");

        chatsList = (RecyclerView) Root.findViewById(R.id.chat_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter=new ChatListRecycleViewAdapter(listContacts);
        chatsList.setAdapter(adapter);
        progressBar =(ProgressBar) Root.findViewById(R.id.progressBar);



        /*FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(ChatsRef,String.class)
                .build();

        adapter=
                new FirebaseRecyclerAdapter<String, ChatListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ChatListViewHolder holder, int position, @NonNull String model) {
                        final String usersIDs = getRef(position).getKey();

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    final String[] Fname = new String[1];
                                    final String retUT = snapshot.child("userType").getValue().toString();
                                    if(retUT=="Vlasnik"){
                                        DatabaseReference workshopRef = mDatabase.getReference().child("workshops");
                                        workshopRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                final String name = snapshot.child("name").getValue().toString();
                                                holder.userName.setText(name);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else{
                                        final String retFName = snapshot.child("ime").getValue().toString();
                                        final String retLName = snapshot.child("prezime").getValue().toString();
                                        final String retName = retFName + " "+ retLName;
                                        holder.userName.setText(retName);
                                    }



                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("receiver_id", usersIDs);
                                            chatIntent.putExtra("receiver_name", holder.userName.getText());

                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        return new ChatListViewHolder(view);
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();*/



        return Root;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        //adapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        listContacts.clear();
        DatabaseReference ref = mDatabase.getReference().child("Contacts").child(uID);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Contacts contact = snapshot.getValue(Contacts.class);

                listContacts.add(contact);

                Collections.sort(listContacts,(a,b)->b.compareTo(a));

                adapter.notifyDataSetChanged();

                chatsList.smoothScrollToPosition(0);

                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Contacts contact = snapshot.getValue(Contacts.class);
                listContacts.remove(contact);
                listContacts.add(contact);
                Collections.sort(listContacts,(a,b)->b.compareTo(a));

                adapter.notifyDataSetChanged();

                chatsList.smoothScrollToPosition(0);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()==progressBar.VISIBLE){
                    Toast.makeText(getContext(),"Ne postoje podaci!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        };
        handler.postDelayed(runnable, 2500);
    }

    public static class  ChatListViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;


        public ChatListViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }
}
