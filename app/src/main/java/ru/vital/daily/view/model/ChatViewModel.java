package ru.vital.daily.view.model;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.collection.LongSparseArray;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.enums.MessageContentType;
import ru.vital.daily.enums.Operation;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.ActionRepository;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.DraftRepository;
import ru.vital.daily.repository.MessageRepository;
import ru.vital.daily.repository.api.request.ChatRequest;
import ru.vital.daily.repository.api.request.IdRequest;
import ru.vital.daily.repository.api.request.MessageRequest;
import ru.vital.daily.repository.api.request.MessagesRequest;
import ru.vital.daily.repository.api.response.ErrorResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.Action;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.repository.model.MessageSendModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.StaticData;

import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_CHANGE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND;

public class ChatViewModel extends ViewModel implements Observable {

    public final String settingsChatFragmentTag = "settingsChatFragmentTag",
            settingsMessageFragmentTag = "settingsMessageFragmentTag",
            attachSheetFragmentTag = "attachSheetFragmentTag";

    public final SingleLiveEvent<Void> attachClickedEvent = new SingleLiveEvent<>(),
            emoClickedEvent = new SingleLiveEvent<>(),
            sendClickedEvent = new SingleLiveEvent<>(),
            fabClickedEvent = new SingleLiveEvent<>(),
            audioCancelClickedEvent = new SingleLiveEvent<>(),
            playPauseTopTabCloseClickedEvent = new SingleLiveEvent<>(),
            showSnackbarEvent = new SingleLiveEvent<>();

    public final MutableLiveData<Void> accountsSendEvent = new MutableLiveData<>();

    public final SingleLiveEvent<Media>
            playPauseBottomTabClickedEvent = new SingleLiveEvent<>(),
            playPauseTopTabClickedEvent = new SingleLiveEvent<>(),
            changeMediaClickedEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Long> emitTypeMessageEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<long[]> emitReadMessageEvent = new SingleLiveEvent<>();

    public final MutableLiveData<Boolean> isRecording = new MutableLiveData<>();

    public final MutableLiveData<Message> newMessage = new MutableLiveData<>(),
            changeMessage = new MutableLiveData<>();

    private final MessageRepository messageRepository;

    private final ChatRepository chatRepository;

    private final ActionRepository actionRepository;

    private final DraftRepository draftRepository;

    private final MessagesRequest messagesRequest = new MessagesRequest();

    private final ChatRequest getChatRequest = new ChatRequest();

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private final MessageSendModel sendMessageModel = new MessageSendModel();

    private final List<Message> unreadMessages = new ArrayList<>();

    @Bindable
    private Message currentAudioMessage;

    @Bindable
    private Media currentAudio;

    @Bindable
    private Chat chat;

    @Bindable
    private User anotherUser, profile;

    @Bindable
    private int initialListSize;

    @Bindable
    private boolean typing;

    private Disposable sendDisposable, recordDisposable, audioDisposable;

    private Draft draft;

    private MediaRecorder mediaRecorder;

    private final Subject<List<Message>> unreadMessagesSubject = PublishSubject.create();

    private final Subject<Boolean> onTypeSubject = PublishSubject.create();

    private final Subject<Long> emitTypeSubject = PublishSubject.create();

    @Inject
    public ChatViewModel(MessageRepository messageRepository, ChatRepository chatRepository, ActionRepository actionRepository, DraftRepository draftRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.actionRepository = actionRepository;
        this.draftRepository = draftRepository;
        DisposableProvider.getDisposableItems(actionRepository.getActions(), actions -> {
            for (Action action : actions) {
                Log.i("my_logs", String.valueOf(action.getId()) + " | " + action.getChatId() + " | " + action.getAction() + " | " + action.getMessageIds());
            }
            Log.i("my_logs", "empty list");
        }, throwable -> {
        });
        this.messagesRequest.setPageIndex(0);
        this.profile = StaticData.getData().profile;

        compositeDisposable.add(unreadMessagesSubject
                .filter(messages -> messages.size() > 0)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(messages -> {
                    compositeDisposable.add(messageRepository.readMessages(new ArrayList<>(messages), sendMessageModel.getChatId()).subscribe(itemResponseCustom -> {
                        DisposableProvider.getDisposableItem(itemResponseCustom, ids -> {
                            emitReadMessageEvent.setValue(ids);
                            for (Message message : messages)
                                message.getInfo().setReadAt(new Date());
                        }, throwable -> {

                        });
                    }));
                    unreadMessages.clear();
                }));
        compositeDisposable.add(onTypeSubject
                .filter(aBoolean -> aBoolean)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(aBoolean -> setTyping(!aBoolean)));
        compositeDisposable.add(emitTypeSubject.filter(chatId -> chatId > 0).throttleFirst(1, TimeUnit.SECONDS).subscribe(emitTypeMessageEvent::setValue));
    }

    @Override
    public void onDestroy() {

    }

    public void setDraft(Message message) {
        sendMessageModel.setMessage(message);
    }

    public void getMessages(Consumer<List<Message>> fromDatabase, Consumer<List<Message>> fromApi, long id, long memberId) {
        if (id != 0) getChatRequest.setId(id);
        if (memberId != 0) getChatRequest.setMemberId(memberId);
        isLoading.set(true);
        compositeDisposable.add(sendDisposable = DisposableProvider.getDisposableItem(chatRepository
                .getChat(getChatRequest), chat -> {
            this.chat = chat;
            notifyChanged(BR.chat);
            sendMessageModel.setChatId(chat.getId());
            if (ChatType.dialog.name().equals(chat.getType())) {
                if (chat.getInfo().getLastMessageId() == null)
                    sendDisposable = chatRepository.saveEmptyChat(chat).subscribe();
                if (profile != null && chat.getMembers() != null)
                    for (User user : chat.getMembers())
                        if (user.getId() != profile.getId()) {
                            setAnotherUser(user);
                            break;
                        }
            }
            getMessages(fromDatabase, fromApi);

        }, errorEvent::setValue));
    }

    public void getMessage(Consumer<Message> message, long id, long chatId) {
        DisposableProvider.getDisposableItem(messageRepository.getMessage(new MessageRequest(id, chatId)), newMessage -> {
            message.accept(newMessage);
            messageRepository.saveMessage(newMessage).subscribe();
        }, throwable -> {

        });
    }

    public void loadMore() {
        /*if (messagesRequest.getPageSize() != 100) messagesRequest.setPageSize(100);
        messagesRequest.setPageIndex(messagesRequest.getPageIndex() + 1);
        getMoreMessages();*/
    }

    public void clearExpiredMessages() {
        messageRepository.clearExpiredMessages(604800000L);
    }

    private void getMessages(Consumer<List<Message>> fromDatabase, Consumer<List<Message>> fromApi) {
        messagesRequest.setChatId(chat.getId());
        messagesRequest.setPageSize(messagesRequest.getPageSize() + chat.getInfo().getUnreadMessagesCount());
        compositeDisposable.add(messageRepository.getMessages(messagesRequest).doOnComplete(() -> {
            isLoading.set(false);
        }).subscribe(new ItemsResponseHandler<Message>() {

            @Override
            public void accept(ItemsResponse<Message> itemResponse) throws Exception {
                switch (itemResponse.getStatusCode()) {
                    case 0:
                        initialListSize = itemResponse.getItems().size();
                        messageRepository.updateExpiredMessages(itemResponse.getItems(), System.currentTimeMillis());
                        fromDatabase.accept(itemResponse.getItems());
                        break;
                    case 200:
                        if (initialListSize == 0) {
                            setInitialListSize(itemResponse.getItems().size());
                        }
                        messageRepository.saveMessages(itemResponse.getItems(), System.currentTimeMillis());
                        fromApi.accept(itemResponse.getItems());
                        break;
                    case 401:
                        errorEvent.setValue(new ErrorResponse(itemResponse.getMessage(), itemResponse.getStatusCode()));
                        break;
                    default:
                        errorEvent.setValue(new Throwable(itemResponse.getMessage()));
                }
            }
        }));
    }

    private void getMoreMessages() {
        compositeDisposable.add(messageRepository.getMoreMessages(messagesRequest).subscribe(new ItemsResponseHandler<Message>() {

            @Override
            public void accept(ItemsResponse<Message> itemResponse) {
                switch (itemResponse.getStatusCode()) {
                    case 0:
                        messageRepository.updateExpiredMessages(itemResponse.getItems(), System.currentTimeMillis());
                        break;
                    case 200:
                        messageRepository.saveMessages(itemResponse.getItems(), System.currentTimeMillis());
                        break;
                    case 401:
                        errorEvent.setValue(new ErrorResponse(itemResponse.getMessage(), itemResponse.getStatusCode()));
                        break;
                    default:
                        errorEvent.setValue(new Throwable(itemResponse.getMessage()));
                }
            }
        }));
    }

    public void removeChat() {
        DisposableProvider.getDisposableBase(chatRepository.removeChat(new IdRequest(getChatRequest.getId())), response -> {
        }, throwable -> {
            errorEvent.setValue(throwable);
        });
        chatRepository.removeChat(getChatRequest.getId());
    }

    public void sendMessage(Consumer<Action> actionConsumer) {
        if (sendDisposable != null && !sendDisposable.isDisposed()) return;
        final Message message = sendMessageModel.getMessage();
        message.setSendStatus(null);
        message.setAuthor(profile);
        if (message.getId() > 0) {
            sendDisposable = messageRepository.updateMessage(message).subscribe(count -> {
                Action action = new Action(message.getId(), message.getChatId(), ACTION_MESSAGE_CHANGE);
                sendDisposable = actionRepository.insertAction(action).subscribe(id -> {
                    actionRepository.deleteDoubledActions(id, message.getId(), message.getChatId());
                    action.setId(id);
                    actionConsumer.accept(action);
                }, throwable -> {

                });
            });
            changeMessage.setValue(message);
        } else {
            sendDisposable = messageRepository.saveMessage(message).subscribe(messageId -> {
                message.setId(messageId);
                Action action = new Action(messageId, message.getChatId(), ACTION_MESSAGE_SEND);
                sendDisposable = actionRepository.insertAction(action).subscribe(id -> {
                    actionRepository.deleteDoubledActions(id, message.getId(), message.getChatId());
                    action.setId(id);
                    sendDisposable.dispose();
                    actionConsumer.accept(action);
                }, throwable -> {

                });
            });
            newMessage.postValue(message);
            setInitialListSize(initialListSize++);
        }
        sendMessageModel.toDefault();
    }

    public void retrieveSending(Consumer<Action> actionConsumer, Message message) {
        Action action = new Action(message.getId(), message.getChatId(), message.getShouldSync() ? ACTION_MESSAGE_SEND : ACTION_MESSAGE_CHANGE);
        sendDisposable = actionRepository.insertAction(action).subscribe(id -> {
            actionRepository.deleteDoubledActions(id, message.getId(), message.getChatId());
            action.setId(id);
            actionConsumer.accept(action);
        }, throwable -> {

        });
    }

    public void changeMediaDescription(LongSparseArray<Media> medias, String title) {
        sendMessageModel.setTabTitle(title);
        sendMessageModel.setStringId(R.string.chat_message_edit_media_sign_summary);
        Media media = medias.valueAt(0);
        sendMessageModel.setMediaForChanging(media);
        sendMessageModel.setText(media.getDescription());
        sendMessageModel.setId(-1L);
    }

    public void changeMessage(Message message) {
        sendMessageModel.setMessage(message);
        sendMessageModel.setTabTitle(null);
        sendMessageModel.setTabSubtitle(message.getText());
        sendMessageModel.setId(message.getId());
        sendMessageModel.setMedias(message.getMedias());
    }

    public void replyMessage(Message message) {
        sendMessageModel.setReplyId(message.getId());
        if (MessageContentType.contact.name().equals(message.getContentType())) {
            sendMessageModel.setTabTitle(message.getContent().getName());
            sendMessageModel.setTabSubtitle(message.getContent().getPhone());
        } else if (message.getAccount() != null) {
            sendMessageModel.setTabTitle(message.getAccount().getUname());
            sendMessageModel.setTabSubtitle(message.getAccount().getEmail() != null ? message.getAccount().getEmail() : message.getAccount().getPhone());
            sendMessageModel.setId(-2L);
        } else {
            sendMessageModel.setTabTitle(message.getAuthor().getUname());
            sendMessageModel.setTabSubtitle(message.getText());
        }
        if (message.getMedias() != null && message.getMedias().size() > 0) {
            Media media = message.getMedias().valueAt(0);
            String type = media.getType();
            if (FileType.video.name().equals(type)) {
                sendMessageModel.setStringId(R.string.chat_message_video);
                sendMessageModel.setMediaForChanging(media);
                sendMessageModel.setId(-2L);
            } else if (FileType.image.name().equals(type)) {
                sendMessageModel.setStringId(R.string.chat_message_photo);
                sendMessageModel.setMediaForChanging(media);
                sendMessageModel.setId(-2L);
            } else if (FileType.voice.name().equals(type))
                sendMessageModel.setStringId(R.string.chat_message_voice);
            else sendMessageModel.setStringId(R.string.chat_message_file);
        }
        sendMessageModel.setReply(message);
    }

    public void deleteMessage(Consumer<Action> actionConsumer, Message message, boolean forAll) {
        messageRepository.deleteMessage(message).subscribe();
        Action action = new Action(Arrays.toString(new long[]{message.getId()}), message.getChatId(), Operation.ACTION_MESSAGE_DELETE, forAll);
        compositeDisposable.add(actionRepository.insertAction(action).subscribe(id -> {
            actionRepository.deleteDoubledActions(id, Arrays.toString(new long[]{message.getId()}), message.getChatId());
            action.setId(id);
            actionConsumer.accept(action);
        }, throwable -> {
        }));
    }

    public void deleteMessage(Consumer<Action> actionConsumer, long[] messageIds, boolean forAll) {
        messageRepository.deleteMessages(messageIds, sendMessageModel.getChatId()).subscribe();
        Action action = new Action(Arrays.toString(messageIds), sendMessageModel.getChatId(), Operation.ACTION_MESSAGE_DELETE, forAll);
        compositeDisposable.add(actionRepository.insertAction(action).subscribe(id -> {
            actionRepository.deleteDoubledActions(id, Arrays.toString(messageIds), sendMessageModel.getChatId());
            action.setId(id);
            actionConsumer.accept(action);
        }, throwable -> {
        }));
    }

    public void deleteMessage(Message message) {
        messageRepository.deleteMessage(message).subscribe();
    }

    public void deleteMessages(long[] messageIds) {
        messageRepository.deleteMessages(messageIds, sendMessageModel.getChatId()).subscribe();
    }

    public void recordAudio(File file, String recordInfo) {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(96000);
        mediaRecorder.setOutputFile(file.getPath());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
        /*final int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        audioRecord.startRecording();*/

        recordDisposable = io.reactivex.Observable.interval(203, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    long millis = aLong * 203;
                    sendMessageModel.setText(String.format(recordInfo, (millis / 1000 / 60) % 60, (millis / 1000) % 60, millis % 1000));
                });
        /*audioDisposable = DisposableProvider.getCallable(() -> writeAudioDataToFile(file.getPath(), bufferSize)).subscribe();*/
        sendMessageModel.setAudioFile(new Media(file.getName(), FileType.voice.name(), new MediaModel(file.getPath(), "audio/x-aac")));
        /*List<Media> medias = new ArrayList<>(1);
        medias.add(new Media(new MediaModel(file.getPath(), null)));
        //medias.add(new Media(FileUtil.getFileType(mimeType), new MediaModel(file.getPath(), mimeType)));
        sendMessageModel.setMedias(medias);*/
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (recordDisposable != null)
            recordDisposable.dispose();
        sendMessageModel.setText(null);

    }

    public void onSendClicked() {
        if (isRecording.getValue() != null)
            isRecording.setValue(null);
        else if (sendMessageModel.getId() != null && sendMessageModel.getId() == -1L) {
            changeMediaClickedEvent.setValue(sendMessageModel.getMediaForChanging());
        } else if (sendMessageModel.getText() != null && !sendMessageModel.getText().isEmpty() || sendMessageModel.getMedias() != null && !sendMessageModel.getMedias().isEmpty() || sendMessageModel.getUsers() != null && !sendMessageModel.getUsers().isEmpty())
            sendClickedEvent.call();
        else showSnackbarEvent.call();
    }

    public void updateMedias(Message message, boolean shouldSync) {
        messageRepository.updateMedias(message.getId(), message.getChatId(), message.getMedias(), shouldSync);
    }

    public void sendMessage(LongSparseArray<Media> medias) {
        sendMessageModel.setMedias(medias);
        onSendClicked();
    }

    public void sendAccounts(LongSparseArray<User> users) {
        sendMessageModel.setUsers(users);
        accountsSendEvent.setValue(null);
    }

    public MessagesRequest getMessagesRequest() {
        return messagesRequest;
    }

    public void onAttachClicked() {
        attachClickedEvent.call();
    }

    public void onEmoClicked() {
        emoClickedEvent.call();
    }

    public void onFabClicked() {
        fabClickedEvent.call();
    }

    public boolean onMicroLongClicked() {
        if ((sendMessageModel.getText() == null || sendMessageModel.getText().isEmpty()) && isRecording.getValue() == null)
            isRecording.setValue(true);
        return true;
    }

    public void onStopRecordingClicked() {
        if (isRecording != null && isRecording.getValue())
            isRecording.setValue(false);
    }

    public void onPlayPauseBottomTabClicked() {
        if (currentAudioMessage != null) setCurrentAudioMessage(null, true);
        playPauseBottomTabClickedEvent.setValue(sendMessageModel.getAudioFile());
    }

    public void onPlayPauseTopTabClicked() {
        playPauseTopTabClickedEvent.setValue(currentAudio);
    }

    public void onPlayPauseTopCloseClicked() {
        currentAudio.setPlaying(null);
        setCurrentAudioMessage(null, true);
        setCurrentAudio(null, false);
        playPauseTopTabCloseClickedEvent.call();
    }

    public void onAudioCancelClicked() {
        sendMessageModel.toDefault();
        audioCancelClickedEvent.call();
        isRecording.setValue(null);
    }

    public void onMessageCloseTabClicked() {
        sendMessageModel.toDefault();
    }

    public void notifyChanged(int fieldId) {
        callbacks.notifyChange(this, fieldId);
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void onResume() {
        sendMessageModel.addOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    @Override
    public void onStop() {
        sendMessageModel.removeOnPropertyChangedCallback(onPropertyChangedCallback);
        if (isRecording.getValue() != null && isRecording.getValue())
            isRecording.setValue(false);
        if (isRecording.getValue() == null)
            takeDraft();
    }

    public Draft takeDraft() {
        if (draft == null) {
            if (sendMessageModel.getMedias() != null && sendMessageModel.getMedias().size() > 0 || sendMessageModel.getText() != null && !sendMessageModel.getText().isEmpty()) {
                draft = new Draft(sendMessageModel.getChatId(), sendMessageModel.getMessage());
                draftRepository.insert(draft);
            } else {
                draftRepository.delete(sendMessageModel.getChatId());
                draft = new Draft(sendMessageModel.getChatId());
            }
        }
        return draft;
    }

    public void readMessage(Message message) {
        DisposableProvider.doCallable(() -> {
            if (message != null && message.getAuthor() != null && message.getAuthor().getId() != profile.getId() && message.getInfo().getReadAt() == null)
                unreadMessages.add(message);
            unreadMessagesSubject.onNext(unreadMessages);
            return true;
        });
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public User getAnotherUser() {
        return anotherUser;
    }

    public void setAnotherUser(User anotherUser) {
        this.anotherUser = anotherUser;
        notifyChanged(BR.anotherUser);
    }

    public int getInitialListSize() {
        return initialListSize;
    }

    public void setInitialListSize(int initialListSize) {
        this.initialListSize = initialListSize;
        notifyChanged(BR.initialListSize);
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
        notifyChanged(BR.typing);
        onTypeSubject.onNext(typing);
    }

    public boolean getTyping() {
        return typing;
    }

    public MessageSendModel getSendMessageModel() {
        return sendMessageModel;
    }

    public Message getCurrentAudioMessage() {
        return currentAudioMessage;
    }

    public void setCurrentAudioMessage(Message currentAudioMessage, boolean shouldNotify) {
        this.currentAudioMessage = currentAudioMessage;
        if (shouldNotify) notifyChanged(BR.currentAudioMessage);
    }

    public void notifyCurrentAudioMessage() {
        notifyChanged(BR.currentAudioMessage);
    }

    public void setCurrentAudio(Media currentAudio, boolean shouldNotify) {
        this.currentAudio = currentAudio;
        if (shouldNotify) notifyChanged(BR.currentAudio);
    }

    public Media getCurrentAudio() {
        return currentAudio;
    }

    public Subject<Long> getEmitTypeSubject() {
        return emitTypeSubject;
    }

    private Observable.OnPropertyChangedCallback onPropertyChangedCallback = new OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
                case BR.text:
                    emitTypeSubject.onNext(sendMessageModel.getChatId());
                    break;
            }
        }
    };
}
