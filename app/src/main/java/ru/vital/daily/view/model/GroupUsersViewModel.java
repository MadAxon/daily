package ru.vital.daily.view.model;

import android.net.Uri;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.AddMembersRequest;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.FileUtil;

public class GroupUsersViewModel extends ViewModel implements Observable {

    public final String avatarSheetFragmentTag = "avatarSheetFragmentTag";

    public final SingleLiveEvent<Void> coverClickEvent = new SingleLiveEvent<>();

    public final MutableLiveData<List<User>> users = new MutableLiveData<>();

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Bindable
    private Uri avatar;

    private final ChatRepository chatRepository;

    private final Api api;

    private Disposable groupDisposable;

    private final ChatSaveModel chatSaveModel;

    private final AddMembersRequest addingMembersRequest;

    @Inject
    public GroupUsersViewModel(ChatRepository chatRepository, ChatSaveModel chatSaveModel, AddMembersRequest addingMembersRequest, Api api) {
        this.chatRepository = chatRepository;
        this.chatSaveModel = chatSaveModel;
        this.api = api;
        this.chatSaveModel.setType(ChatType.conversation.name());
        this.addingMembersRequest = addingMembersRequest;
    }

    public void createGroup(File cover, String mimeType, Consumer<Long> onContinue) {
        if (groupDisposable != null && !groupDisposable.isDisposed()) return;
        isLoading.set(true);
        if (chatSaveModel.getCoverId() == null && cover != null)
            compositeDisposable.add(groupDisposable = DisposableProvider.getDisposableItem(api.uploadMedia(MultipartBody.Part.createFormData("file", cover.getName(), RequestBody.create(MediaType.parse(mimeType), cover)),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), FileUtil.getFileType(mimeType))), media -> {
                        chatSaveModel.setCoverId(media.getId());
                        createGroup(onContinue, cover);
                    },
                    throwable -> createGroup(onContinue, cover)));
        else createGroup(onContinue, cover);
    }

    private void createGroup(Consumer<Long> onContinue, File cover) {
        compositeDisposable.add(groupDisposable = DisposableProvider.getDisposableItem(chatRepository.createChat(new ItemRequest<>(chatSaveModel))
                , chat -> {
                    if (chat.getCover() != null && cover != null)
                        chat.getCover().getFiles().get(0).setUrl(cover.getPath());
                    addingMembersRequest.setId(chat.getId());
                    chatRepository.saveChat(chat);
                    addMembersToChat(aVoid -> onContinue.accept(chat.getId()));
                }, throwable -> {
                    isLoading.set(false);
                    errorEvent.setValue(throwable);
                }));
    }

    private void addMembersToChat(Consumer<Void> onContinue) {
        if (users.getValue() != null && addingMembersRequest.getMemberIds() == null) {
            long[] userIds = new long[users.getValue().size()];
            for (int i = 0; i < userIds.length; i++)
                userIds[i] = users.getValue().get(i).getId();
            addingMembersRequest.setMemberIds(userIds);
        }

        compositeDisposable.add(groupDisposable = chatRepository.addMembersToChat(addingMembersRequest,
                onContinue,
                throwable -> {
                    onContinue.accept(null);
                }));
    }

    public void onCoverClicked() {
        coverClickEvent.call();
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public void notifyChanged(int fieldId) {
        callbacks.notifyChange(this, fieldId);
    }

    public Uri getAvatar() {
        return avatar;
    }

    public void setAvatar(Uri avatar) {
        this.avatar = avatar;
    }

    public ChatSaveModel getChatSaveModel() {
        return chatSaveModel;
    }
}
