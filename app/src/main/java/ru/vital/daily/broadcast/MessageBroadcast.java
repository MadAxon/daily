package ru.vital.daily.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.Disposable;
import ru.vital.daily.repository.ActionRepository;
import ru.vital.daily.service.MessageService;

import static ru.vital.daily.enums.Operation.ACTION_INTERNET_OFFLINE;
import static ru.vital.daily.enums.Operation.ACTION_INTERNET_ONLINE;
import static ru.vital.daily.enums.Operation.ACTION_JOB_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND;

public class MessageBroadcast extends BroadcastReceiver {

    public static final String
            JOB_ID_EXTRA = "JOB_ID_EXTRA",
            CHAT_ID_EXTRA = "CHAT_ID_EXTRA",
            MESSAGE_ID_EXTRA = "MESSAGE_ID_EXTRA",
            MESSAGE_IDS_EXTRA = "MESSAGE_IDS_EXTRA",
            MESSAGE_FOR_ALL_EXTRA = "MESSAGE_FOR_ALL_EXTRA",
            MEDIA_ID_EXTRA = "MEDIA_ID_EXTRA",
            MEDIA_DESCRIPTION_EXTRA = "MEDIA_DESCRIPTION_EXTRA",
            IS_ONLINE_EXTRA = "IS_ONLINE_EXTRA";

    @Inject
    ActionRepository actionRepository;

    @Inject
    ConnectivityManager connectivityManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);
        if (intent.getAction() != null) {
            Intent intentService = new Intent(context, MessageService.class);
            intentService.putExtra(JOB_ID_EXTRA, intent.getLongExtra(JOB_ID_EXTRA, 0));
            intentService.putExtra(CHAT_ID_EXTRA, intent.getLongExtra(CHAT_ID_EXTRA, 0));
            intentService.setAction(intent.getAction());
            switch (intent.getAction()) {
                case ACTION_MESSAGE_DELETE:
                    intentService.putExtra(MESSAGE_IDS_EXTRA, idsStringToLongArray(intent.getStringExtra(MESSAGE_IDS_EXTRA)));
                    intentService.putExtra(MESSAGE_FOR_ALL_EXTRA, intent.getBooleanExtra(MESSAGE_FOR_ALL_EXTRA, false));
                    context.startService(intentService);
                    break;
                case ACTION_MESSAGE_SEND:
                    intentService.putExtra(MESSAGE_ID_EXTRA, intent.getLongExtra(MESSAGE_ID_EXTRA, 0));
                    MessageService.enqueueWork(context, intentService);
                    //context.startService(intentService);
                    break;
                case ACTION_MESSAGE_CHANGE:
                    intentService.putExtra(MESSAGE_ID_EXTRA, intent.getLongExtra(MESSAGE_ID_EXTRA, 0));
                    /*DisposableProvider.getDisposableItem(actionRepository.getAction(intent.getLongExtra(MessageBroadcast.JOB_ID_EXTRA, 0)),
                            job -> {
                                //startService(context, job, intent.getLongArrayExtra(MESSAGE_IDS_EXTRA), intent.getBooleanExtra(MESSAGE_FOR_ALL_EXTRA, false));
                            },
                            throwable -> {
                                //jobFinished(params, false);
                            });*/
                    MessageService.enqueueWork(context, intentService);
                    //context.startService(intentService);
                    break;
                case ACTION_MESSAGE_CANCEL:
                    intentService.putExtra(MESSAGE_ID_EXTRA, intent.getLongExtra(MESSAGE_ID_EXTRA, 0));
                    intentService.putExtra(MEDIA_ID_EXTRA, intent.getLongExtra(MEDIA_ID_EXTRA, 0));
                    MessageService.enqueueWork(context, intentService);
                case ACTION_JOB_DELETE:
                    if (intent.getLongExtra(MESSAGE_ID_EXTRA, 0) != 0)
                        deleteAction(intent.getLongExtra(MESSAGE_ID_EXTRA, 0),
                                intent.getLongExtra(CHAT_ID_EXTRA, 0));
                    else deleteAction(intent.getLongArrayExtra(MESSAGE_IDS_EXTRA),
                            intent.getLongExtra(CHAT_ID_EXTRA, 0));
                    break;
                case ACTION_INTERNET_ONLINE:
                    Log.i("my_logs", "online work");
                    MessageService.enqueueWork(context, intentService);
                    break;
                case ACTION_INTERNET_OFFLINE:
                    Log.i("my_logs", "offline work");
                    MessageService.enqueueWork(context, intentService);
                    break;
                case ACTION_MEDIA_UPLOAD_CANCEL:
                    intentService.putExtra(MESSAGE_ID_EXTRA, intent.getLongExtra(MESSAGE_ID_EXTRA, 0));
                    intentService.putExtra(MEDIA_ID_EXTRA, intent.getLongExtra(MEDIA_ID_EXTRA, 0));
                    MessageService.enqueueWork(context, intentService);
                    break;
                case ACTION_MEDIA_CHANGE:
                    intentService.putExtra(MEDIA_ID_EXTRA, intent.getLongExtra(MEDIA_ID_EXTRA, 0));
                    intentService.putExtra(MEDIA_DESCRIPTION_EXTRA, intent.getStringExtra(MEDIA_DESCRIPTION_EXTRA));
                    MessageService.enqueueWork(context, intentService);
                    break;
            }
        }
    }

    private void deleteAction(long messageId, long chatId) {
        Disposable disposable = actionRepository.deleteAction(messageId, chatId).subscribe(integer -> {
            Log.i("my_logs", "The count is " + integer);
        });
    }

    private void deleteAction(long[] messageIds, long chatId) {
        actionRepository.deleteAction(Arrays.toString(messageIds), chatId);
    }

    private long[] idsStringToLongArray(String messageIds) {
        String[] items = messageIds.
                replaceAll("\\[", "").
                replaceAll("\\]", "").
                replaceAll("\\s", "").
                split(",");

        long[] results = new long[items.length];

        for (int i = 0; i < items.length; i++) {
            try {
                results[i] = Long.parseLong(items[i]);
            } catch (NumberFormatException nfe) {
                //NOTE: write something here if you need to recover from formatting errors
            };
        }
        return results;
    }
}
