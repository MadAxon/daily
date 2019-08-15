package ru.vital.daily.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import ru.vital.daily.util.NotificationSparseArray;

public class NotificationBroadcast extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_DELETE = "ACTION_NOTIFICATION_DELETE",
                            ID_EXTRA = "ID_EXTRA";

    @Inject
    NotificationSparseArray notifications;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case ACTION_NOTIFICATION_DELETE:
                    Log.i("my_logs", "ACTION_NOTIFICATION_DELETE with id " + intent.getIntExtra(ID_EXTRA, 0));
                    notifications.sparseArray.remove(intent.getIntExtra(ID_EXTRA, 0));
                    break;
            }
    }
}
