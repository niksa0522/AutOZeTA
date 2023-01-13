package com.example.autozeta.Basic.UI.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autozeta.ActivityCheckClass;
import com.example.autozeta.Adapters.ChatRecycleViewAdapter;
import com.example.autozeta.Logic;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Contacts;
import data.Message;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageSenderID;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    private TextView userName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
    private ImageButton  SendFilesButton;
    private Button SendMessageButton;
    private EditText messageText;

    private String imageUrl;

    private Toolbar ChatToolBar;
    private final List<Message> messagesList = new ArrayList<>();

    private RecyclerView messagesListView;
    private ChatRecycleViewAdapter adapter;
    private LinearLayoutManager manager;
    private DatabaseReference mFirebaseDatabaseReference;
    private ProgressBar progressBar;
    private Handler handler;
    private Runnable runnable;


    private String currentTime,currentDate,messagePushID,messageSenderRef,messageReceiverRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        messageSenderID= mAuth.getCurrentUser().getUid();


        messageReceiverID=getIntent().getExtras().get("receiver_id").toString();
        messageReceiverName=getIntent().getExtras().get("receiver_name").toString();

        InitControllers();


        userName.setText(messageReceiverName);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);

        DatabaseReference ref = mDatabase.getReference().child("messages").child(messageSenderID).child(messageReceiverID);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message messages = snapshot.getValue(Message.class);

                messagesList.add(messages);

                progressBar.setVisibility(ProgressBar.INVISIBLE);

                adapter.notifyDataSetChanged();

                messagesListView.smoothScrollToPosition(messagesListView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });



        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if(progressBar.getVisibility()==progressBar.VISIBLE){
                    Toast.makeText(getApplicationContext(),"Ne postoje podaci!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        };
        handler.postDelayed(runnable, 2500);

    }

    private void SendMessage() {
        String message = messageText.getText().toString();
        DatabaseReference RootRef = mDatabase.getReference();
        if (message.isEmpty())
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "messages/" + messageReceiverID + "/" + messageSenderID;


            DatabaseReference userMessageKeyRef = RootRef.child("messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentD = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentD.format(calendar.getTime());

            SimpleDateFormat currentT = new SimpleDateFormat("HH:mm");
            currentTime = currentT.format(calendar.getTime());


            Map messageTextBody = new HashMap();
            messageTextBody.put("text", message);
            messageTextBody.put("fromUserId", messageSenderID);
            messageTextBody.put("toUserId", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", currentTime);
            messageTextBody.put("date", currentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {

                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageText.setText("");
                }
            });

            Contacts contactUpdateSender = new Contacts(message,calendar.getTime(),messageSenderID);
            Contacts contactUpdateRecever = new Contacts("Ti: "+message,calendar.getTime(),messageReceiverID);
            mDatabase.getReference().child("Contacts").child(messageReceiverID).child(messageSenderID).setValue(contactUpdateSender);
            mDatabase.getReference().child("Contacts").child(messageSenderID).child(messageReceiverID).setValue(contactUpdateRecever);

            mDatabase.getReference().child("users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final String[] Fname = new String[1];
                    final String retUT = snapshot.child("userType").getValue().toString();
                    if(retUT.equals("Vlasnik")){
                        final String retName = snapshot.child("imeRadionce").getValue().toString();
                        Logic.SendNotification(messageReceiverID,"Nova poruka","Korisnik " +retName+ ": "+message,getApplicationContext(),ChatActivity.class,messageSenderID);
                    }
                    else{
                        final String retFName = snapshot.child("ime").getValue().toString();
                        final String retLName = snapshot.child("prezime").getValue().toString();
                        final String retName = retFName + " "+ retLName;
                        Logic.SendNotification(messageReceiverID,"Nova poruka","Korisnik " +retName+ ": "+message,getApplicationContext(),ChatActivity.class,messageSenderID);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    private void InitControllers()
    {
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);*/

        userName = findViewById(R.id.name);
        SendMessageButton = findViewById(R.id.sendButton);
        SendFilesButton = findViewById(R.id.send_files_btn);
        messageText=(EditText) findViewById(R.id.messageEditText);

        adapter=new ChatRecycleViewAdapter(messagesList);
        messagesListView = findViewById(R.id.private_messages_list_of_users);
        manager=new LinearLayoutManager(this);
        messagesListView.setLayoutManager(manager);
        messagesListView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityCheckClass.SetActivity(this);
        ActivityCheckClass.setOtherUser(messageReceiverID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        ActivityCheckClass.clearOtherUser(messageReceiverID);
        ActivityCheckClass.ClearActivity(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ChatActivity", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == 2) {
            if (resultCode == -1) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d("ChatActivity", "Uri: " + uri.toString());


                     messageSenderRef = "messages/" + messageSenderID + "/" + messageReceiverID;
                     messageReceiverRef = "messages/" + messageReceiverID + "/" + messageSenderID;


                    DatabaseReference userMessageKeyRef = mDatabase.getReference().child("messages")
                            .child(messageSenderID).child(messageReceiverID).push();

                    messagePushID = userMessageKeyRef.getKey();

                    StorageReference storageReference =
                            FirebaseStorage.getInstance()
                                    .getReference(mAuth.getCurrentUser().getUid())
                                    .child(messagePushID)
                                    .child(uri.getLastPathSegment());

                    putImageInStorage(storageReference, uri, messagePushID);





                }
            }
        }
    }


    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(ChatActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        imageUrl=task.getResult().toString();
                                                        Calendar calendar = Calendar.getInstance();

                                                        SimpleDateFormat currentD = new SimpleDateFormat("MMM dd, yyyy");
                                                        currentDate = currentD.format(calendar.getTime());

                                                        SimpleDateFormat currentT = new SimpleDateFormat("HH:mm");
                                                        currentTime = currentT.format(calendar.getTime());

                                                        Map messageTextBody = new HashMap();
                                                        messageTextBody.put("imageUrl", imageUrl);
                                                        messageTextBody.put("fromUserId", messageSenderID);
                                                        messageTextBody.put("toUserId", messageReceiverID);
                                                        messageTextBody.put("messageID", messagePushID);
                                                        messageTextBody.put("time", currentTime);
                                                        messageTextBody.put("date", currentDate);

                                                        Map messageBodyDetails = new HashMap();
                                                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                                        messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                                                        mDatabase.getReference().updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else
                                                                {
                                                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                }
                                                                messageText.setText("");
                                                            }
                                                        });
                                                        Contacts contactUpdateSender = new Contacts("Slika",calendar.getTime(),messageSenderID);
                                                        Contacts contactUpdateRecever = new Contacts("Ti: Slika",calendar.getTime(),messageReceiverID);
                                                        mDatabase.getReference().child("Contacts").child(messageReceiverID).child(messageSenderID).setValue(contactUpdateSender);
                                                        mDatabase.getReference().child("Contacts").child(messageSenderID).child(messageReceiverID).setValue(contactUpdateRecever);

                                                        mDatabase.getReference().child("users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                final String[] Fname = new String[1];
                                                                final String retUT = snapshot.child("userType").getValue().toString();
                                                                if(retUT.equals("Vlasnik")){
                                                                    final String retName = snapshot.child("imeRadionce").getValue().toString();
                                                                    Logic.SendNotification(messageReceiverID,"Nova poruka","Korisnik " +retName+ " je poslao sliku",getApplicationContext(),ChatActivity.class,messageSenderID);
                                                                }
                                                                else{
                                                                    final String retFName = snapshot.child("ime").getValue().toString();
                                                                    final String retLName = snapshot.child("prezime").getValue().toString();
                                                                    final String retName = retFName + " "+ retLName;
                                                                    Logic.SendNotification(messageReceiverID,"Nova poruka","Korisnik " +retName+ " je poslao sliku",getApplicationContext(),ChatActivity.class,messageSenderID);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    }
                                                }
                                            });
                        } else {
                            Log.w("ChatActivity", "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }
}
