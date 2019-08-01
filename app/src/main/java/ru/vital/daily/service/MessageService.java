package ru.vital.daily.service;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.broadcast.MessageBroadcast;
import ru.vital.daily.repository.ActionRepository;
import ru.vital.daily.repository.MessageRepository;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.api.ProgressRequestBody;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.request.MessageRemoveRequest;
import ru.vital.daily.repository.api.response.handler.ItemResponseHandler;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.Action;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.MediaEditModel;
import ru.vital.daily.repository.model.MessageSendModel;
import ru.vital.daily.util.DisposableProvider;

import static ru.vital.daily.enums.Operation.ACTION_INTERNET_ONLINE;
import static ru.vital.daily.enums.Operation.ACTION_JOB_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_END;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_FAILED;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_PROGRESS;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_START;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_SUCCESS;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND_FAILED;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND_UPDATED;

public class MessageService extends JobIntentService {

    @Inject
    ConnectivityManager connectivityManager;

    @Inject
    MessageRepository messageRepository;

    @Inject
    Api api;

    @Inject
    ActionRepository actionRepository;

    @Inject
    ItemRequest<MessageSendModel> itemRequest;

    @Inject
    DailySocket dailySocket;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean cancelled;

    private Message currentMessage;

    private long[] mediaIds;

    private int uploadsSize, currentUploadPosition;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, MessageService.class, 1904, intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null)
            switch (intent.getAction()) {
                case ACTION_MESSAGE_CANCEL:
                    if (currentMessage != null && currentMessage.getId() == intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0) && currentMessage.getChatId() == intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0)) {
                        Log.i("my_logs", "cancelled");
                        compositeDisposable.clear();
                        cancelled = true;
                    }
                    break;
                case ACTION_MEDIA_UPLOAD_CANCEL:
                    if (currentMessage != null && currentMessage.getId() == intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0) && currentMessage.getChatId() == intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0)) {
                        Log.i("my_logs", "upload cancel");
                        long mediaId = intent.getLongExtra(MessageBroadcast.MEDIA_ID_EXTRA, 0);
                        if (mediaId == mediaIds[currentUploadPosition]) {
                            compositeDisposable.clear();
                        }
                        currentMessage.getMedias().remove(mediaId);
                    }
                    break;
            }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("my_logs", compositeDisposable.toString());
        if (cancelled)
            cancelled = false;
        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case ACTION_MESSAGE_CHANGE:
                    initSending(intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), true);
                    break;
                case ACTION_MESSAGE_SEND:
                    initSending(intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), false);
                    break;
                case ACTION_MESSAGE_DELETE:
                    deleteMessage(intent.getLongArrayExtra(MessageBroadcast.MESSAGE_IDS_EXTRA), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), intent.getBooleanExtra(MessageBroadcast.MESSAGE_FOR_ALL_EXTRA, false));
                    break;
                case ACTION_INTERNET_ONLINE:
                    compositeDisposable.add(actionRepository.getActions().subscribe(new ItemsResponseHandler<>(actions -> {
                        for (Action action : actions) {
                            Intent broadcastIntent = new Intent(this, MessageBroadcast.class);
                            broadcastIntent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                            broadcastIntent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                            broadcastIntent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, action.getMessageIds());
                            broadcastIntent.putExtra(MessageBroadcast.MESSAGE_FOR_ALL_EXTRA, action.getForAll());
                            broadcastIntent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, action.getMessageId());
                            broadcastIntent.setAction(action.getAction());
                            sendBroadcast(broadcastIntent);
                        }
                    }, throwable -> {

                    }), throwable -> {

                    }));
                    break;
                case ACTION_MEDIA_CHANGE:
                    changeMediaDescription(intent.getLongExtra(MessageBroadcast.MEDIA_ID_EXTRA, 0), intent.getStringExtra(MessageBroadcast.MEDIA_DESCRIPTION_EXTRA));
                    break;
            }
        Log.i("my_logs", "onHandleWork()");
    }

    private void initSending(final long messageId, final long chatId, final boolean shouldChangeMessage) {
        Log.i("my_logs", "------------- sending");
        Log.i("my_logs", String.valueOf(messageId));
        compositeDisposable.add(messageRepository.getMessage(messageId, chatId, !shouldChangeMessage).subscribe(new ItemResponseHandler<>(message -> {
            currentMessage = message;
            if (message.getMedias() != null && message.getMedias().size() > 0)
                uploadMediasAndSend(currentMessage, shouldChangeMessage);
            else sendMessage(currentMessage, shouldChangeMessage);
        }, throwable -> {
            deleteOperation(messageId, chatId);
        }), throwable -> {
            deleteOperation(messageId, chatId);
            Log.i("my_logs", throwable.getLocalizedMessage());
        }));
    }

    private void uploadMediasAndSend(final Message message, final boolean shouldChangeMessage) {
        uploadsSize = message.getMedias().size();
        mediaIds = new long[message.getMedias().size()];
        for (int i = 0; i < uploadsSize; i++)
            mediaIds[i] = message.getMedias().keyAt(i);
        uploadMedia(message, shouldChangeMessage);
        /*final List<Single<Object>> files = new ArrayList<>();
        final int size = message.getMedias().size();
        for (int i = 0; i < size; i++) {
            final Media media = message.getMedias().get(i);
            if (media.getId() != 0 || cancelled) continue;
            final int mediaPosition = i;
            final Intent progressIntent = new Intent(ACTION_MEDIA_UPLOAD_PROGRESS);
            progressIntent.putExtra(ChatActivity.MESSAGE_ID_EXTRA, message.getId());
            progressIntent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
            progressIntent.putExtra(ChatActivity.MEDIA_POSITION_EXTRA, mediaPosition);
            files.add(mediaRepository.uploadMedia(MultipartBody.Part.createFormData("file", media.getName(), new ProgressRequestBody(RequestBody.create(MediaType.parse(media.getFiles().get(0).getType()), new File(media.getFiles().get(0).getUrl())), (bytes, contentLength, done) -> {
                        progressIntent.putExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, done ? -1f : (float) bytes / contentLength);
                        sendBroadcast(progressIntent);
                    })),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), media.getType()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).doOnError(throwable -> {
                        Log.i("my_logs", "uploading failed");
                        media.setUploaded(false);
                        progressIntent.setAction(ACTION_MEDIA_UPLOAD_FAILED);
                        sendBroadcast(progressIntent);
                    })
                    .doOnSuccess(mediaItemResponse -> {
                        if (mediaItemResponse.getStatusCode() == 200) {
                            Media uploadedMedia = mediaItemResponse.getItem();
                            media.setId(uploadedMedia.getId());
                            uploadedMedia.setFiles(media.getFiles());
                            mediaRepository.saveMedia(uploadedMedia).subscribe();
                            progressIntent.setAction(ACTION_MEDIA_UPLOAD_SUCCESS);
                            progressIntent.putExtra(ChatActivity.MEDIA_ID_EXTRA, uploadedMedia.getId());
                            sendBroadcast(progressIntent);
                        }
                    })
                    .onErrorResumeNext(Single.just(new ItemResponse<>()))
                    .flatMap(mediaItemResponse -> Single.just(new Object()).onErrorReturnItem(new Object())));
        }
        files.add(Single.just(new Object()));
        compositeDisposable.add(Single.zip(files, objects -> new Object()).cache().flatMap(o -> {
            Log.i("my_logs", "uploading success");
            Intent intent = new Intent(ACTION_MEDIA_UPLOAD_NEXT);
            intent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
            sendBroadcast(intent);
            if (message.getMedias().size() > 0)
                messageRepository.updateMedias(message.getId(), message.getChatId(), message.getMedias());
            sendMessage(message, shouldChangeMessage);
            return Single.just(new Object());
        }).subscribe());*/
    }

    private void uploadMedia(Message message, boolean shouldChangeMessage) {
        if (cancelled) return;
        if (mediaIds.length <= currentUploadPosition) {
            Intent intent = new Intent(ACTION_MEDIA_UPLOAD_END);
            intent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
            sendBroadcast(intent);
            int failedUploads = uploadsSize - message.getMedias().size(); // some medias might be deleted while uploading
            if (message.getText() == null || message.getText().isEmpty())
                for (int i = 0; i < message.getMedias().size(); i++)
                    if (message.getMedias().valueAt(i).getId() <= 0)
                        failedUploads++;
            if (failedUploads >= uploadsSize)
                handleSendingError(message);
            else {
                messageRepository.updateMedias(message.getId(), message.getChatId(), message.getMedias(), true);
                sendMessage(message, shouldChangeMessage);
            }
            return;
        }
        Media media = message.getMedias().get(mediaIds[currentUploadPosition]);
        if (media == null || media.getId() > 0) {
            currentUploadPosition++;
            uploadMedia(message, shouldChangeMessage);
        } else {
            Intent progressIntent = new Intent(ACTION_MEDIA_UPLOAD_START);
            progressIntent.putExtra(ChatActivity.MESSAGE_ID_EXTRA, message.getId());
            progressIntent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
            sendBroadcast(progressIntent);

            progressIntent.setAction(ACTION_MEDIA_UPLOAD_PROGRESS);
            progressIntent.putExtra(ChatActivity.MEDIA_ID_EXTRA, media.getId());
            progressIntent.putExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, 0f);
            sendBroadcast(progressIntent);
            Subject<Media> subject = PublishSubject.create();


            File file = new File(media.getFiles().get(0).getUrl());
            Log.i("my_logs", "uploaded started with type " + media.getFiles().get(0).getType() + ", url " + media.getFiles().get(0).getUrl() + " and name " + media.getName());

            compositeDisposable.add(api.uploadMedia(MultipartBody.Part.createFormData("file", /*null*/media.getName(), new ProgressRequestBody(RequestBody.create(MediaType.parse(media.getFiles().get(0).getType()), file), (bytes, contentLength, done) -> {
                        progressIntent.putExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, done ? 1f : (float) bytes / contentLength);
                        sendBroadcast(progressIntent);
                        //Log.i("my_logs", String.valueOf((float) bytes / contentLength));
                    })),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), media.getType()))
                    .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).doOnDispose(() -> {
                        Log.i("my_logs", "do on dispose");
                        subject.onNext(new Media(0));
                        //currentUploadPosition++;
                        //uploadMedia(message, shouldChangeMessage);
                    }).subscribe(new ItemResponseHandler<>(uploadedMedia -> {
                        Log.i("my_logs", "upload success");
                        /*uploadedMedia.setFiles(media.getFiles());
                        mediaRepository.saveMedia(uploadedMedia).subscribe();
                        progressIntent.setAction(ACTION_MEDIA_UPLOAD_SUCCESS);
                        progressIntent.putExtra(ChatActivity.MEDIA_ID_EXTRA, uploadedMedia.getId());
                        sendBroadcast(progressIntent);*/
                        //currentUploadPosition++;
                        //uploadMedia(message, shouldChangeMessage);
                        subject.onNext(uploadedMedia);
                    }, throwable -> {
                        Log.i("my_logs", "uploading failed " + (throwable != null ? throwable.toString() + " " + throwable.getMessage() : ""));
                        subject.onNext(new Media(-1));
                        progressIntent.setAction(ACTION_MEDIA_UPLOAD_FAILED);
                        sendBroadcast(progressIntent);
                        //currentUploadPosition++;
                        //uploadMedia(message, shouldChangeMessage);
                    }), throwable -> {
                        subject.onNext(new Media(-1));
                        Log.i("my_logs", "uploading failed" + (throwable != null ? throwable.getMessage() : ""));
                        progressIntent.setAction(ACTION_MEDIA_UPLOAD_FAILED);
                        sendBroadcast(progressIntent);
                        //currentUploadPosition++;
                        //uploadMedia(message, shouldChangeMessage);
                    }));
            Media uploadedMedia = subject.blockingFirst();
            subject.onComplete();
            Log.i("my_logs", "subject.blockingFirst()");
            if (uploadedMedia.getId() == -1) {
                progressIntent.setAction(ACTION_MEDIA_UPLOAD_FAILED);
                sendBroadcast(progressIntent);
                currentUploadPosition++;
                uploadMedia(message, shouldChangeMessage);
            } else if (uploadedMedia.getId() == 0) {
                currentUploadPosition++;
                uploadMedia(message, shouldChangeMessage);
            } else {
                uploadedMedia.setFiles(media.getFiles());
                //mediaRepository.saveMedia(uploadedMedia).subscribe();
                progressIntent.setAction(ACTION_MEDIA_UPLOAD_SUCCESS);
                progressIntent.putExtra(ChatActivity.MEDIA_ID_EXTRA, media.getId());
                progressIntent.putExtra(ChatActivity.MEDIA_ID_NEW_EXTRA, uploadedMedia.getId());
                sendBroadcast(progressIntent);
                media.setId(uploadedMedia.getId());
                currentUploadPosition++;
                uploadMedia(message, shouldChangeMessage);
            }
        }
    }

    private void sendMessage(final Message message, boolean shouldChangeMessage) {
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        MessageSendModel sendModel = new MessageSendModel(message);
        if (shouldChangeMessage) sendModel.setId(message.getId());
        itemRequest.setItem(sendModel);
        if (cancelled) return;
        /*compositeDisposable.add(messageRepository.sendMessage(itemRequest).subscribe(new ItemResponseHandler<>(sentMessage -> {
            Log.i("my_logs", "sending success");
            if (!shouldChangeMessage) {
                Intent intent = new Intent(ACTION_MESSAGE_SEND_UPDATED);
                intent.putExtra(ChatActivity.MESSAGE_ID_NEW_EXTRA, sentMessage.getId());
                intent.putExtra(ChatActivity.MESSAGE_ID_OLD_EXTRA, message.getId());
                sendBroadcast(intent);

                messageRepository.deleteMessage(message).subscribe();
                sentMessage.setCreatedAt(message.getCreatedAt());
                messageRepository.saveMessage(sentMessage).subscribe();
            }
            deleteOperation(message);
        }, throwable -> {
            handleSendingError(message);
        }), throwable -> {
            if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException) {
                Log.i("my_logs", "sending failed | connection issue");
            } else {
                Log.i("my_logs", "sending failed " + throwable.getMessage());
                handleSendingError(message);
            }
        }));*/
        Subject<Message> subject = PublishSubject.create();
        compositeDisposable.add(DisposableProvider.getDisposableItem(messageRepository.sendMessage(itemRequest).doOnDispose(() -> subject.onNext(new Message(0))), sentMessage -> {
            Log.i("my_logs", "sending success");
            dailySocket.emitSendMessage(sentMessage.getId());
            subject.onNext(sentMessage);
        }, throwable -> {
            if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException) {
                Log.i("my_logs", "sending failed | connection issue");
                subject.onNext(new Message(0));
            } else {
                Log.i("my_logs", "sending failed " + throwable.getMessage());
                subject.onNext(message);
            }
        }));
        Message sentMessage = subject.blockingFirst();
        if (!sentMessage.getShouldSync()) {
            if (!shouldChangeMessage) {
                Intent intent = new Intent(ACTION_MESSAGE_SEND_UPDATED);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, sentMessage.getChatId());
                intent.putExtra(ChatActivity.MESSAGE_ID_NEW_EXTRA, sentMessage.getId());
                intent.putExtra(ChatActivity.MESSAGE_ID_OLD_EXTRA, message.getId());
                sendBroadcast(intent);

                messageRepository.deleteMessage(message).subscribe();
                sentMessage.setCreatedAt(message.getCreatedAt());
                messageRepository.saveMessage(sentMessage).subscribe();
            }
            deleteOperation(message);
        } else if (sentMessage.getId() != 0) {
            handleSendingError(message);
        }
        subject.onComplete();
    }

    private void handleSendingError(final Message message) {
        message.setSendStatus(false);
        messageRepository.updateSendStatus(message);
        Intent intent = new Intent(ACTION_MESSAGE_SEND_FAILED);
        intent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
        intent.putExtra(ChatActivity.MESSAGE_ID_EXTRA, message.getId());
        sendBroadcast(intent);
        deleteOperation(message);
    }

    private void deleteOperation(final Message message) {
        Intent messageBroadcastIntent = new Intent(this, MessageBroadcast.class);
        messageBroadcastIntent.setAction(ACTION_JOB_DELETE);
        messageBroadcastIntent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, message.getId());
        messageBroadcastIntent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, message.getChatId());
        sendBroadcast(messageBroadcastIntent);
    }

    private void deleteOperation(long messageId, long chatId) {
        Intent messageBroadcastIntent = new Intent(this, MessageBroadcast.class);
        messageBroadcastIntent.setAction(ACTION_JOB_DELETE);
        messageBroadcastIntent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, messageId);
        messageBroadcastIntent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, chatId);
        sendBroadcast(messageBroadcastIntent);
    }

    private void deleteMessage(long[] messageIds, long chatId, boolean forAll) {
        DisposableProvider.getDisposableBase(messageRepository.deleteMessages(new MessageRemoveRequest(messageIds, chatId, forAll)), response -> {
            dailySocket.emitRemoveMessage(chatId, messageIds);
            Log.i("my_logs", "delete message is successful");
        }, throwable -> {
            Log.i("my_logs", throwable.getLocalizedMessage());
        });
        Intent messageBroadcastIntent = new Intent(this, MessageBroadcast.class);
        messageBroadcastIntent.setAction(ACTION_JOB_DELETE);
        messageBroadcastIntent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, messageIds);
        messageBroadcastIntent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, chatId);
        sendBroadcast(messageBroadcastIntent);
    }

    private void changeMediaDescription(long mediaId, String description) {
        compositeDisposable.add(api.editMedia(new ItemRequest<>(new MediaEditModel(mediaId, description))).subscribe(new ItemResponseHandler<>(media -> {
            Log.i("my_logs", "media with id " + mediaId + " was changed successful");
        }, throwable -> {
            Log.i("my_logs", "media with id " + mediaId + " cauth a ERROR:" + throwable.getLocalizedMessage());
        })));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        Log.i("my_logs", "Intent Service onDestroy");
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.i("my_logs", "onStopCurrentWork");
        return super.onStopCurrentWork();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("my_logs", "onTaskRemoved");
    }
}
