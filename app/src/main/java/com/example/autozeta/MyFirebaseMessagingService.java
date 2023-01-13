package com.example.autozeta;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        sendRegistrationToServer(s);
    }




    private void sendRegistrationToServer(String token){
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://autozeta-cc1c0-default-rtdb.europe-west1.firebasedatabase.app");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            mDatabase.getReference().child("Tokens").child(firebaseUser.getUid()).setValue(token);
            FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid());
        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationID = new Random().nextInt(3000);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(notificationManager);
            }
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_message_24);

            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "admin_channel")
                    .setSmallIcon(R.drawable.ic_baseline_message_24)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(remoteMessage.getData().get("title"))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(remoteMessage.getData().get("message")))
                    .setAutoCancel(true)
                    .setSound(notificationSoundUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
            }
            try {
                String rm = remoteMessage.getFrom();
                if(rm.contains(firebaseUser.getUid())) {
                    if (ActivityCheckClass.GetCurrentActivity() != null) {
                        String s = remoteMessage.getData().get("key1");
                        String s1 = ActivityCheckClass.GetCurrentActivity().getClass().toString();
                        if (s.equals(s1) && ActivityCheckClass.getOtherUser().equals(remoteMessage.getData().get("key2"))) {

                        } else {
                            notificationManager.notify(notificationID, notificationBuilder.build());
                        }
                    } else {
                        notificationManager.notify(notificationID, notificationBuilder.build());
                    }
                }
            }
            catch (Exception ex){

            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "Nova notifikacija";
        String adminChannelDescription = "Notifikacija";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("admin_channel", adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
