package com.example.myjobportalapplication.data_Model;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.commonActivities.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

//        String Id = message.getData().get("senderID");
//        String name = message.getData().get("name");
//        String token = message.getData().get("FCM TOKEN");
//
//        int notificationId = new Random().nextInt();
//        String channelId = "chat_message";
//
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra("Id", Id);
//        intent.putExtra("name", name);
//        intent.putExtra("token", token);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
//        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
//        builder.setContentTitle(name);
//        builder.setContentText(message.getData().get("message"));
//        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
//                message.getData().get("messsage")
//        ));
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence channelName = "Chat Message";
//            String channelDescription = "This notification channel is used for chat notification";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
//            channel.setDescription(channelDescription);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
