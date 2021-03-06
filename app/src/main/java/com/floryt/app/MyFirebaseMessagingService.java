package com.floryt.app;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgServiceLog";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            notifyUser(remoteMessage);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void notifyUser(RemoteMessage remoteMessage) {
        HashMap<String, String> data = new HashMap<>(remoteMessage.getData());
        Log.d(TAG, data.toString());
        Class activity;
        NotificationCompat.Builder mBuilder;
        switch (data.get("messageType")){
            case "identity":
                activity = IdentityVerificationActivity.class;
                mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_floryt_24dp)
                                .setContentTitle("Identity verification request")
                                .setContentText("Verify your identity to log in to the computer");
                break;
            case "permission":
                activity = PermissionRequestActivity.class;
                mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_floryt_24dp)
                                .setContentTitle("Permission request")
                                .setContentText(String.format("%s wants to log in to your computer", data.get("guestName") == null ? "Someone" : data.get("guestName")));
                break;
            default:
                return;
        }
        if (mBuilder != null){
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1, mBuilder.build());
        }
        Intent intent = new Intent(this, activity);
        intent.putExtra("data", data);
        startActivity(intent);
    }
}