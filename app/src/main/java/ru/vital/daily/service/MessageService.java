package ru.vital.daily.service;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.File;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;

import dagger.android.AndroidInjection;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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
import ru.vital.daily.repository.api.request.MessageRequest;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.handler.ItemResponseHandler;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.Action;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.MediaEditModel;
import ru.vital.daily.repository.model.MessageSendModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.MediaProgressHelper;

import static ru.vital.daily.enums.Operation.ACTION_INTERNET_ONLINE;
import static ru.vital.daily.enums.Operation.ACTION_JOB_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_UPLOAD_SUCCESS;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_FORWARD;
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

    @Inject
    MediaProgressHelper mediaProgressHelper;

    private Disposable progressDisposable;

    private final Subject<Float> progressSubject = PublishSubject.create();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final Set<Long> actions = new HashSet<>();

    private boolean cancelled;

    private Message currentMessage;

    private long[] mediaIds;

    private int uploadsSize, currentUploadPosition;

    private long jobId;

    public static void enqueueWork(Context context, Intent intent) {
        if (!actions.contains(intent.getLongExtra(MessageBroadcast.JOB_ID_EXTRA, 0)))
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
        jobId = intent.getLongExtra(MessageBroadcast.JOB_ID_EXTRA, 0);
        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case ACTION_MESSAGE_CHANGE:
                    actions.add(jobId);
                    initSending(intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), true);
                    break;
                case ACTION_MESSAGE_SEND:
                    actions.add(jobId);
                    initSending(intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), false);
                    break;
                case ACTION_MESSAGE_DELETE:
                    actions.add(jobId);
                    deleteMessage(intent.getLongArrayExtra(MessageBroadcast.MESSAGE_IDS_EXTRA), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), intent.getBooleanExtra(MessageBroadcast.MESSAGE_FOR_ALL_EXTRA, false));
                    break;
                case ACTION_INTERNET_ONLINE:
                    actions.add(jobId);
                    compositeDisposable.add(actionRepository.getActions().subscribe(new ItemsResponseHandler<>(actions -> {
                        for (Action action : actions) {
                            Intent broadcastIntent = new Intent(this, MessageBroadcast.class);
                            broadcastIntent.putExtra(MessageBroadcast.JOB_ID_EXTRA, action.getId());
                            broadcastIntent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, action.getChatId());
                            broadcastIntent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, action.getMessageIds());
                            broadcastIntent.putExtra(MessageBroadcast.MESSAGE_FOR_ALL_EXTRA, action.getForAll());
                            broadcastIntent.putExtra(MessageBroadcast.MESSAGE_ID_EXTRA, action.getMessageId());
                            broadcastIntent.putExtra(MessageBroadcast.FROM_CHAT_ID_EXTRA, action.getFromChatId());
                            broadcastIntent.setAction(action.getAction());
                            sendBroadcast(broadcastIntent);
                        }
                    }, throwable -> {

                    }), throwable -> {

                    }));
                    break;
                case ACTION_MEDIA_CHANGE:
                    actions.add(jobId);
                    changeMediaDescription(intent.getLongExtra(MessageBroadcast.MESSAGE_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.MEDIA_ID_EXTRA, 0), intent.getStringExtra(MessageBroadcast.MEDIA_DESCRIPTION_EXTRA));
                    break;
                case ACTION_MESSAGE_FORWARD:
                    actions.add(jobId);
                    forwardMessages(intent.getLongArrayExtra(MessageBroadcast.MESSAGE_IDS_EXTRA), intent.getLongExtra(MessageBroadcast.FROM_CHAT_ID_EXTRA, 0), intent.getLongExtra(MessageBroadcast.CHAT_ID_EXTRA, 0));
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
            deleteOperation();
            //deleteOperation(messageId, chatId);
        }), throwable -> {
            deleteOperation();
            //deleteOperation(messageId, chatId);
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
            if (progressDisposable == null)
                progressDisposable = progressSubject.throttleLast(1, TimeUnit.SECONDS).subscribe(progress -> {
                    Media progressMedia = mediaProgressHelper.getMedia(media.getId());
                    if (progressMedia != null)
                        progressMedia.setProgress(progress);
                });

            Intent progressIntent = new Intent();
            progressIntent.putExtra(ChatActivity.MESSAGE_ID_EXTRA, message.getId());
            progressIntent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
            //sendBroadcast(progressIntent);

            //progressIntent.setAction(ACTION_MEDIA_UPLOAD_PROGRESS);
            //progressIntent.putExtra(ChatActivity.MEDIA_ID_EXTRA, media.getId());
            //sendBroadcast(progressIntent);
            Subject<Media> subject = PublishSubject.create();

            File file = new File(media.getFiles().get(0).getUrl());
            Log.i("my_logs", "uploaded started with type " + media.getFiles().get(0).getType() + ", url " + media.getFiles().get(0).getUrl() + " and name " + media.getName());

            Single<ItemResponse<Media>> singleUpload;
            if (media.getDescription() != null && !media.getDescription().isEmpty())
                singleUpload = api.uploadMedia(MultipartBody.Part.createFormData("file", /*null*/media.getName(), new ProgressRequestBody(RequestBody.create(MediaType.parse(media.getFiles().get(0).getType()), file), (bytes, contentLength, done) -> {
                            progressSubject.onNext(done ? 1f : (float) bytes / contentLength);
                            //progressIntent.putExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, done ? 1f : (float) bytes / contentLength);
                            //sendBroadcast(progressIntent);
                            Log.i("my_logs", String.valueOf((float) bytes / contentLength));
                        })),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"), media.getType()),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"), media.getDescription()));
            else
                singleUpload = api.uploadMedia(MultipartBody.Part.createFormData("file", /*null*/media.getName(), new ProgressRequestBody(RequestBody.create(MediaType.parse(media.getFiles().get(0).getType()), file), (bytes, contentLength, done) -> {
                            progressSubject.onNext(done ? 1f : (float) bytes / contentLength);
                            //progressIntent.putExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, done ? 1f : (float) bytes / contentLength);
                            //sendBroadcast(progressIntent);
                            Log.i("my_logs", String.valueOf((float) bytes / contentLength));
                        })),
                        RequestBody.create(MediaType.parse("application/json; charset=utf-8"), media.getType()));

            compositeDisposable.add(singleUpload
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
                        Media progressMedia = mediaProgressHelper.getMedia(media.getId());
                        if (progressMedia != null)
                            progressMedia.setProgress(null);
                        //currentUploadPosition++;
                        //uploadMedia(message, shouldChangeMessage);
                    }), throwable -> {
                        subject.onNext(new Media(-1));
                        Log.i("my_logs", "uploading failed" + (throwable != null ? throwable.getMessage() : ""));
                        Media progressMedia = mediaProgressHelper.getMedia(media.getId());
                        if (progressMedia != null)
                            progressMedia.setProgress(null);
                        //currentUploadPosition++;
                        //uploadMedia(message, shouldChangeMessage);
                    }));
            Media uploadedMedia = subject.blockingFirst();
            subject.onComplete();
            Log.i("my_logs", "subject.blockingFirst()");
            if (uploadedMedia.getId() == -1) {
                currentUploadPosition++;
                mediaProgressHelper.remove(media.getId());
                uploadMedia(message, shouldChangeMessage);
            } else if (uploadedMedia.getId() == 0) {
                currentUploadPosition++;
                mediaProgressHelper.remove(media.getId());
                uploadMedia(message, shouldChangeMessage);
            } else {

                Media progressMedia = mediaProgressHelper.getMedia(media.getId());
                if (progressMedia != null) {
                    progressMedia.setId(uploadedMedia.getId());
                    progressMedia.setProgress(null);
                    mediaProgressHelper.remove(media.getId());
                }
                uploadedMedia.setFiles(media.getFiles());
                media.setId(uploadedMedia.getId());
                //mediaRepository.saveMedia(uploadedMedia).subscribe();
                progressIntent.setAction(ACTION_MEDIA_UPLOAD_SUCCESS);
                progressIntent.putExtra(ChatActivity.MEDIA_ID_EXTRA, media.getId());
                //progressIntent.putExtra(ChatActivity.MEDIA_ID_NEW_EXTRA, uploadedMedia.getId());
                sendBroadcast(progressIntent);
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
            dailySocket.emitSendMessage(new long[]{sentMessage.getId()}, sentMessage.getChatId());
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
            } else if (sentMessage.getUpdatedAt() != null) {
                Log.i("my_logs", "updateMessageUpdatedAt" + sentMessage.getId());
                messageRepository.updateMessageUpdatedAt(sentMessage.getId(), sentMessage.getChatId(), sentMessage.getUpdatedAt());
                Intent intent = new Intent(ACTION_MESSAGE_CHANGE);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, sentMessage.getChatId());
                intent.putExtra(ChatActivity.MESSAGE_ID_EXTRA, sentMessage.getId());
                intent.putExtra(ChatActivity.DATE_UPDATED_EXTRA, sentMessage.getUpdatedAt().getTime());
                sendBroadcast(intent);
            }
            //deleteOperation(message);
            deleteOperation();
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
        //deleteOperation(message);
        deleteOperation();
    }

/*    private void deleteOperation(final Message message) {
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

    private void deleteOperation(long[] messageIds, long chatId) {
        Intent messageBroadcastIntent = new Intent(this, MessageBroadcast.class);
        messageBroadcastIntent.setAction(ACTION_JOB_DELETE);
        messageBroadcastIntent.putExtra(MessageBroadcast.MESSAGE_IDS_EXTRA, messageIds);
        messageBroadcastIntent.putExtra(MessageBroadcast.CHAT_ID_EXTRA, chatId);
        sendBroadcast(messageBroadcastIntent);
    }*/

    private void deleteOperation() {
        Intent messageBroadcastIntent = new Intent(this, MessageBroadcast.class);
        messageBroadcastIntent.setAction(ACTION_JOB_DELETE);
        messageBroadcastIntent.putExtra(MessageBroadcast.JOB_ID_EXTRA, jobId);
        sendBroadcast(messageBroadcastIntent);
        actions.remove(jobId);
    }

    private void deleteMessage(long[] messageIds, long chatId, boolean forAll) {
        DisposableProvider.getDisposableBase(messageRepository.deleteMessages(new MessageRemoveRequest(messageIds, chatId, forAll)), response -> {
            if (forAll)
                dailySocket.emitRemoveMessage(chatId, messageIds);
            Log.i("my_logs", "delete message is successful");
        }, throwable -> {
            Log.i("my_logs", throwable.getLocalizedMessage());
        });
        //deleteOperation(messageIds, chatId);
        deleteOperation();
    }

    private void changeMediaDescription(long messageId, long chatId, long mediaId, String description) {
        compositeDisposable.add(api.editMedia(new ItemRequest<>(new MediaEditModel(mediaId, description))).subscribe(new ItemResponseHandler<>(media -> {
            compositeDisposable.add(messageRepository.getMessage(messageId, chatId, false).subscribe(new ItemResponseHandler<>(message -> {
                Media savedMedia = message.getMedias().get(mediaId);
                if (savedMedia != null)
                    savedMedia.setDescription(description);
                messageRepository.updateMedias(messageId, chatId, message.getMedias(), false);
                deleteOperation();
            }, throwable -> {
                deleteOperation();
            })));
        }, throwable -> {
            deleteOperation();
        })));
    }

    private void forwardMessages(long[] messageIds, long fromChatId, long toChatId) {
        compositeDisposable.add(messageRepository.getMessages(messageIds, toChatId).subscribe(new ItemsResponseHandler<>(messages -> {
            final int size = messages.size();
            long[] ids = new long[size];
            for (int i = 0; i < size; i++)
                ids[i] = messages.get(i).getForwardId();
            compositeDisposable.add(messageRepository.forwardMessages(ids, fromChatId, toChatId).subscribe(new ItemsResponseHandler<>(sentMessages -> {
                messageRepository.deleteMessages(messageIds, toChatId).subscribe();
                final int sentMessagesSize = sentMessages.size();
                long[] sentMessageIds = new long[size];
                int position = 0;

                for (int i = 0; i < sentMessagesSize; i++) {
                    Message newMessage = sentMessages.get(i);
                    for (int j = position; j < sentMessagesSize; j++) {
                        position++;
                        if (ids[j] == newMessage.getForwardId()) {
                            sentMessageIds[j] = newMessage.getId();
                            newMessage.setCreatedAt(messages.get(j).getCreatedAt());
                            break;
                        } else {
                            sentMessageIds[j] = 0;
                            //j++;
                        }
                    }
                }

                dailySocket.emitSendMessage(sentMessageIds, toChatId);

                Intent intent = new Intent(ACTION_MESSAGE_FORWARD);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, toChatId);
                intent.putExtra(ChatActivity.MESSAGE_IDS_NEW_EXTRA, sentMessageIds);
                intent.putExtra(ChatActivity.MESSAGE_IDS_OLD_EXTRA, messageIds);
                sendBroadcast(intent);

                messageRepository.saveMessages(sentMessages, toChatId);
                //deleteOperation(messageIds, toChatId);
                deleteOperation();
            }, throwable -> {
                //deleteOperation(messageIds, toChatId);
                deleteOperation();
            })));
        }, throwable -> {

        })));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        if (progressDisposable != null)
            progressDisposable.dispose();
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
