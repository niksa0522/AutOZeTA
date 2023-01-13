package com.example.autozeta.Adapters;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autozeta.Basic.UI.chat.ChatActivity;
import com.example.autozeta.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.List;

import data.Message;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecycleViewAdapter extends RecyclerView.Adapter<ChatRecycleViewAdapter.ChatMessageViewHolder> {


    private List<Message> messageList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");

    public ChatRecycleViewAdapter (List<Message> userMessagesList)
    {
        this.messageList = userMessagesList;
    }

    public class ChatMessageViewHolder extends RecyclerView.ViewHolder{
        public TextView senderMessageText, receiverMessageText;
        public ImageView messageSenderImage,messageReceiverImage;
        public TextView senderTime,receiverTime;

        public ChatMessageViewHolder(View v){
            super(v);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            messageReceiverImage = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderImage = itemView.findViewById(R.id.message_sender_image_view);
            senderTime=(TextView) itemView.findViewById(R.id.sender_messsage_time);
            receiverTime = (TextView) itemView.findViewById(R.id.receiver_message_time);
        }
    }



    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Message messages = messageList.get(position);

        String fromUserID = messages.getFromUserId();
        String type;
        if(messages.getText()!=null)
            type="text";
        else
            type="image";
        DatabaseReference usersRef = mDatabase.getReference().child("users").child(fromUserID);


        holder.receiverMessageText.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderImage.setVisibility(View.GONE);
        holder.messageReceiverImage.setVisibility(View.GONE);
        holder.senderTime.setVisibility(View.GONE);
        holder.receiverTime.setVisibility(View.GONE);

        if(type.equals("text")){
            if(fromUserID.equals(messageSenderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderTime.setVisibility(View.VISIBLE);

                holder.senderMessageText.setText(messages.getText());
                holder.senderTime.setText(messages.getTime()+" - " + messages.getDate());


            }
            else{
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverTime.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getText());
                holder.receiverTime.setText(messages.getTime()+" - " + messages.getDate());
            }
        }
        else{
            if(fromUserID.equals(messageSenderId)) {
                String imageUrl = messages.getImageUrl();
                if (imageUrl.startsWith("gs://")) {
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(imageUrl);
                    storageReference.getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();
                                        Glide.with(holder.itemView.getContext())
                                                .load(downloadUrl)
                                                .into(holder.messageSenderImage);
                                    } else {
                                        Log.w("Chat activity", "Getting download url was not successful.",
                                                task.getException());
                                    }
                                }
                            });
                } else {
                    Glide.with(holder.itemView.getContext())
                            .load(messages.getImageUrl())
                            .into(holder.messageSenderImage);
                }
                holder.messageSenderImage.setVisibility(ImageView.VISIBLE);
                holder.senderMessageText.setVisibility(TextView.GONE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW,R.id.message_sender_image_view);
                holder.senderTime.setLayoutParams(p);
                holder.senderTime.setVisibility(View.VISIBLE);
                holder.senderTime.setText(messages.getTime()+" - " + messages.getDate());
            }
            else{
                String imageUrl = messages.getImageUrl();
                if (imageUrl.startsWith("gs://")) {
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReferenceFromUrl(imageUrl);
                    storageReference.getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();
                                        Glide.with(holder.itemView.getContext())
                                                .load(downloadUrl)
                                                .into(holder.messageReceiverImage);
                                    } else {
                                        Log.w("Chat activity", "Getting download url was not successful.",
                                                task.getException());
                                    }
                                }
                            });
                } else {
                    Glide.with(holder.itemView.getContext())
                            .load(messages.getImageUrl())
                            .into(holder.messageReceiverImage);
                }
                holder.messageReceiverImage.setVisibility(ImageView.VISIBLE);
                holder.senderMessageText.setVisibility(TextView.GONE);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW,R.id.message_receiver_image_view);
                holder.receiverTime.setLayoutParams(p);
                holder.receiverTime.setVisibility(View.VISIBLE);
                holder.receiverTime.setText(messages.getTime()+" - " + messages.getDate());
            }
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
