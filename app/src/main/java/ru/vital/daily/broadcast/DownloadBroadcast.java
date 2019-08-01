package ru.vital.daily.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dagger.android.AndroidInjection;
import ru.vital.daily.service.DownloadService;

import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_PROGRESS;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_START;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_SUCCESS;

public class DownloadBroadcast extends BroadcastReceiver {

    public static final String MEDIA_ID_EXTRA = "MEDIA_ID_EXTRA";

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        if (intent.getAction() != null) {
            Intent serviceIntent = new Intent(context, DownloadService.class);
            serviceIntent.putExtra(MEDIA_ID_EXTRA, intent.getLongExtra(MEDIA_ID_EXTRA, 0));
            serviceIntent.setAction(intent.getAction());
            DownloadService.enqueueWork(context, serviceIntent);
        }
    }
}
