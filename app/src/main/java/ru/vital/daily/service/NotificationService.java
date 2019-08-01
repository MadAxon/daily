package ru.vital.daily.service;

import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vital.daily.R;
import ru.vital.daily.enums.PushNotificationType;

public class NotificationService extends FirebaseMessagingService {

    public NotificationService() {
    }

    public static void subscribe(String topic) {
        for (PushNotificationType type: PushNotificationType.values()) {
            FirebaseMessaging.getInstance().subscribeToTopic(String.format("%s-android-%s", topic, type.name()));
            Log.i("my_logs", String.format("%s-android-%s", topic, type.name()));
        }
    }

    public void unsubscribe(String topic) {
        for (PushNotificationType type: PushNotificationType.values())
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.format("%s-android-%s", topic, type.name()));
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("my_logs", "onMessageReceived ");
        if (remoteMessage.getData().size() > 0) {
            Log.i("my_logs", "onMessageReceived " + remoteMessage.getData().get("type"));
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(this, "DailyNotification")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText("")
                .setAutoCancel(true);

    }
}
