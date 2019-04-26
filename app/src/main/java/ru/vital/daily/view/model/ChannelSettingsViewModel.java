package ru.vital.daily.view.model;

import android.app.Application;
import android.net.Uri;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.vital.daily.R;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.MediaRepository;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.FileUtil;

public class ChannelSettingsViewModel extends ViewModel {

    public final String contactFragmentTag = "contactFragmentTag";

    private ChatSaveModel chatSaveModel;

    private Uri avatar;

    private final ChatRepository chatRepository;

    private final MediaRepository mediaRepository;

    private String accessKey;

    private Disposable channelDisposable;

    @Inject
    public ChannelSettingsViewModel(ChatRepository chatRepository, MediaRepository mediaRepository, KeyRepository keyRepository) {
        this.chatRepository = chatRepository;
        this.mediaRepository = mediaRepository;
        DisposableProvider.getDisposableItem(keyRepository.getCurrentKey(),
                key -> {
                    accessKey = key.getAccessKey();
                },
                throwable -> {
                    accessKey = "";
                    //keyRepository.clearKeys();
                });
    }

    public void createChannel(File cover, String mimeType, Consumer<Long> chatId) {
        if (channelDisposable != null && !channelDisposable.isDisposed()) return;
        isLoading.set(true);
        if (chatSaveModel.getCoverId() == null && cover != null)
            compositeDisposable.add(channelDisposable = DisposableProvider.getDisposableItem(mediaRepository.uploadMedia(MultipartBody.Part.createFormData("file", cover.getName(), RequestBody.create(MediaType.parse(mimeType), cover)),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), accessKey),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), FileUtil.getFileType(mimeType))), media -> {
                        mediaRepository.saveMedia(media);
                        chatSaveModel.setCoverId(media.getId());
                        createChannel(chatId);
                    },
                    throwable -> createChannel(chatId)));
        else createChannel(chatId);
    }

    private void createChannel(Consumer<Long> chatId) {
        compositeDisposable.add(channelDisposable = DisposableProvider.getDisposableItem(chatRepository.createChat(new ItemRequest<>(chatSaveModel))
                , chat -> {
                    chatRepository.saveChat(chat);
                    chatId.accept(chat.getId());
                    isLoading.set(false);
                }, throwable -> {
                    isLoading.set(false);
                    errorEvent.setValue(throwable);
                }));
    }

    public void onRadioChanged(int checkedId) {
        switch (checkedId) {
            case R.id.on:
                chatSaveModel.setType(ChatType.publicChannel.name());
                break;
            case R.id.off:
                chatSaveModel.setType(ChatType.privateChannel.name());
                break;
        }
    }

    public ChatSaveModel getChatSaveModel() {
        return chatSaveModel;
    }

    public void setChatSaveModel(ChatSaveModel chatSaveModel) {
        this.chatSaveModel = chatSaveModel;
    }

    public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
        chatSaveModel.setCoverId(null);
    }
}
