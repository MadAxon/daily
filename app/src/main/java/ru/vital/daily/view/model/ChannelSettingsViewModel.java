package ru.vital.daily.view.model;

import android.net.Uri;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.vital.daily.R;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.FileUtil;

public class ChannelSettingsViewModel extends ViewModel {

    public final String contactFragmentTag = "contactFragmentTag";

    private ChatSaveModel chatSaveModel;

    private Uri avatar;

    private final ChatRepository chatRepository;

    private final Api api;

    private Disposable channelDisposable;

    @Inject
    public ChannelSettingsViewModel(ChatRepository chatRepository, Api api) {
        this.chatRepository = chatRepository;
        this.api = api;
    }

    public void createChannel(File cover, String mimeType, Consumer<Long> chatId) {
        if (channelDisposable != null && !channelDisposable.isDisposed()) return;
        isLoading.set(true);
        if (chatSaveModel.getCoverId() == null && cover != null)
            compositeDisposable.add(channelDisposable = DisposableProvider.getDisposableItem(api.uploadMedia(MultipartBody.Part.createFormData("file", cover.getName(), RequestBody.create(MediaType.parse(mimeType), cover)),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), FileUtil.getFileType(mimeType))), media -> {
                        chatSaveModel.setCoverId(media.getId());
                        createChannel(chatId, cover);
                    },
                    throwable -> createChannel(chatId, cover)));
        else createChannel(chatId, cover);
    }

    private void createChannel(Consumer<Long> chatId, File cover) {
        compositeDisposable.add(channelDisposable = DisposableProvider.getDisposableItem(chatRepository.createChat(new ItemRequest<>(chatSaveModel))
                , chat -> {
                    if (chat.getCover() != null && cover != null)
                        chat.getCover().getFiles().get(0).setUrl(cover.getPath());
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
