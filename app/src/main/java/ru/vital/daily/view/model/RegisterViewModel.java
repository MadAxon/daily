package ru.vital.daily.view.model;

import android.net.Uri;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.SyncContactsModel;
import ru.vital.daily.repository.model.ProfileSaveModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.util.StaticData;

public class RegisterViewModel extends ViewModel implements Observable {

    @Bindable
    private Uri avatar;

    public final String avatarSheetFragmentTag = "avatarSheetFragmentTag";

    public SingleLiveEvent<Void> avatarClickedEvent = new SingleLiveEvent<>();

    public final ProfileSaveModel profileModel;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    private final Api api;

    private final KeyRepository keyRepository;

    private final UserRepository userRepository;

    private final SyncContactsModel syncContactsModel;

    private Key key;

    private Disposable registerDisposable;

    @Inject
    public RegisterViewModel(Api api, KeyRepository keyRepository, UserRepository userRepository, ProfileSaveModel profileModel, SyncContactsModel syncContactsModel) {
        this.api = api;
        this.keyRepository = keyRepository;
        this.userRepository = userRepository;
        DisposableProvider.getDisposableItem(keyRepository.getCurrentKey(),
                key -> {
                    this.key = key;
                },
                throwable -> {
                    keyRepository.clearKeys();
                });
        this.profileModel = profileModel;
        this.syncContactsModel = syncContactsModel;
    }

    public void onAvatarClick() {
        if (!isLoading.get())
            avatarClickedEvent.call();
    }

    public void registerUser(File avatar, String mimeType, Consumer<User> successfulRegister) {
        if (registerDisposable != null && !registerDisposable.isDisposed()) return;
        isLoading.set(true);
        if (profileModel.getAvatarId() == null) {
            compositeDisposable.add(registerDisposable = DisposableProvider.getDisposableItem(api.uploadMedia(MultipartBody.Part.createFormData("file", avatar.getName(), RequestBody.create(MediaType.parse(mimeType), avatar)),

                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), FileUtil.getFileType(mimeType))), media -> {
                profileModel.setAvatarId(media.getId());
                registerUser(successfulRegister, avatar);
            }, throwable -> {
                isLoading.set(false);
                errorEvent.setValue(throwable);
            }));
        } else {
            registerUser(successfulRegister, avatar);
        }
    }

    public void registerUser(Consumer<User> successfulRegister, File avatar) {
        compositeDisposable.add(registerDisposable = DisposableProvider.getDisposableItem(userRepository.saveProfile(new ItemRequest<>(profileModel)), user -> {
            StaticData.init(key, user);
            if (user.getAvatar() != null && avatar != null )
                user.getAvatar().getFiles().get(0).setUrl(avatar.getPath());
            userRepository.saveUser(user);
            keyRepository.updateCurrentUserId(user.getId());
            successfulRegister.accept(user);
        }, throwable -> {
            isLoading.set(false);
            errorEvent.setValue(throwable);
        }));
    }

    public void syncPhones(List<String> phones, Consumer<Void> onContinue) {
        syncContactsModel.setPhones(phones);
        compositeDisposable.add(DisposableProvider.getDisposableBase(api.syncContacts(syncContactsModel), s -> onContinue.accept(null), throwable -> onContinue.accept(null)));
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
        profileModel.setAvatarId(null);
    }
}
