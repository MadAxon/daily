package ru.vital.daily.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.android.AndroidInjection;
import ru.vital.daily.R;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.broadcast.NotificationBroadcast;
import ru.vital.daily.enums.MessageContentType;
import ru.vital.daily.enums.PushNotificationType;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.NotificationSparseArray;
import ru.vital.daily.util.StaticData;

import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_NOTIFICATION_OPEN;

@Singleton
public class NotificationService extends FirebaseMessagingService {

    private final String BIG_TEXT_EXTRA = "BIG_TEXT_EXTRA",
            NUMBER_EXTRA = "NUMBER_EXTRA";

    @Inject
    NotificationManager notificationManager;

    @Inject
    NotificationSparseArray notifications;

    @Inject
    public NotificationService() {

    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    public void subscribe(String topic) {
        for (PushNotificationType type : PushNotificationType.values()) {
            FirebaseMessaging.getInstance().subscribeToTopic(String.format("%s-android-%s", topic, type.name()));
            Log.i("my_logs", String.format("%s-android-%s", topic, type.name()));
        }
    }

    public void unsubscribe(String topic) {
        for (PushNotificationType type : PushNotificationType.values())
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.format("%s-android-%s", topic, type.name()));
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("my_logs", "onMessageReceived ");
        if (remoteMessage.getData().size() > 0)
            switch (PushNotificationType.valueOf(remoteMessage.getData().get("_type"))) {
                case post_save:

                    break;
                case story_save:

                    break;
                case profile_save:

                    break;
                case common:

                    break;
                case post_like_toggle:

                    break;
                case chat_message_send:
                    int chatId = Integer.valueOf(remoteMessage.getData().get("chatId"));
                    String authorUname = remoteMessage.getData().get("authorUname");
                    String imageUrl = remoteMessage.getData().get("authorAvatarUrl");

                    String body;
                    if (remoteMessage.getData().get("medias") != null && remoteMessage.getData().get("medias").length() > 5)
                        body = getString(R.string.chat_message_file);
                    else if (MessageContentType.contact.name().equals(remoteMessage.getData().get("contentType")) || remoteMessage.getData().get("accountId") != null)
                        body = getString(R.string.chat_message_contact);
                    else body = remoteMessage.getData().get("_body");
                    long when = 0;
                    try {
                        Date createdAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).parse(remoteMessage.getData().get("createdAt"));
                        when = createdAt.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (notifications.sparseArray.get(chatId) == null)
                        loadImage(
                                chatId,
                                authorUname != null ? authorUname : remoteMessage.getData().get("_title"),
                                body,
                                imageUrl,
                                when != 0 ? when : System.currentTimeMillis(),
                                remoteMessage.getData().get("authorId")
                        );
                    else
                        updateNotification(chatId, remoteMessage.getData().get("_body"), when);
                    break;
                case post_comment_send:

                    break;
                case story_like_toggle:

                    break;
                case project_add_member:

                    break;
                case account_follow_toggle:

                    break;
                case post_comment_like_toggle:

                    break;
            }
    }

    public void cancelNotification(int id) {
        notifications.sparseArray.remove(id);
        notificationManager.cancel(id);
    }

    private void loadImage(int id, String title, String body, String imageUrl, long when, String authorId) {
        if (imageUrl != null)
            Glide.with(this).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    showNotification(resource, id, title, body, when);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    TextDrawable textDrawable = TextDrawable.builder()
                            .beginConfig()
                            .width(96)
                            .height(96)
                            .fontSize(35)
                            .endConfig()
                            .buildRect(title.substring(0, 1), ColorGenerator.MATERIAL.getColor(authorId));
                    showNotification(drawableToBitmap(textDrawable), id, title, body, when);
                }
            });
        else {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .width(96)
                    .height(96)
                    .fontSize(35)
                    .endConfig()
                    .buildRect(title.substring(0, 1), ColorGenerator.MATERIAL.getColor(authorId));
            showNotification(drawableToBitmap(textDrawable), id, title, body, when);
        }
    }

    private void updateNotification(int id, String body, long when) {
        notificationManager.cancel(id);
        NotificationCompat.Builder builder = notifications.sparseArray.get(id);
        Bundle bundle = builder.getExtras();
        int number = bundle.getInt(NUMBER_EXTRA, 1);
        number = number + 1;
        bundle.putInt(NUMBER_EXTRA, number);
        RemoteViews parentView = builder.getBigContentView();
        RemoteViews childView = new RemoteViews(getPackageName(), R.layout.layout_notification_big_item);
        childView.setTextViewText(R.id.textView, body);
        parentView.addView(R.id.container, childView);

        builder.setContentText(getResources().getQuantityString(R.plurals.unread_messages, number, number));
        //builder.setWhen(when);
        builder.setNumber(number);
        builder.setExtras(bundle);
        notificationManager.notify(id, builder.build());
    }

    private void showNotification(Bitmap resource, int id, String title, String body, long when) {
        RemoteViews parentView = new RemoteViews(getPackageName(), R.layout.layout_notification_big);
        RemoteViews childView = new RemoteViews(getPackageName(), R.layout.layout_notification_big_item);
        childView.setTextViewText(R.id.textView, body);
        parentView.addView(R.id.container, childView);

        Intent deleteIntent = new Intent(this, NotificationBroadcast.class);
        deleteIntent.setAction(NotificationBroadcast.ACTION_NOTIFICATION_DELETE);
        deleteIntent.putExtra(NotificationBroadcast.ID_EXTRA, id);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, 0);

        Intent openIntent = new Intent(this, ChatActivity.class);
        openIntent.setAction(ACTION_MESSAGE_NOTIFICATION_OPEN);
        openIntent.putExtra(ChatActivity.CHAT_ID_EXTRA, Long.valueOf(id));
        PendingIntent openPendingIntent = PendingIntent.getActivity(this, 0, openIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("DailyNotification", "Daily", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(NotificationService.this, "DailyNotification")
                .setSmallIcon(R.drawable.ic_send)
                .setLargeIcon(getCircleBitmap(resource))
                .setContentTitle(title)
                .setContentText(body)
                .setShowWhen(true)
                .setWhen(when)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setNumber(1)
                .setDefaults(Notification.DEFAULT_ALL)
                .setCustomBigContentView(parentView)
                .setDeleteIntent(deletePendingIntent)
                .setContentIntent(openPendingIntent);

        Bundle bundle = new Bundle();
        builder.setExtras(bundle);

        notifications.sparseArray.put(id, builder);

        notificationManager.notify(id, builder.build());
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        if (bitmap.isRecycled()) return bitmap;
        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height) {
            output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            int left = (width - height) / 2;
            int right = left + height;
            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);
            r = height / 2;
        } else {
            output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            int top = (height - width) / 2;
            int bottom = top + width;
            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);
            r = width / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }
}
