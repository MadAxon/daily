package ru.vital.daily.view.model;

import android.app.Application;
import android.net.Uri;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.model.ChatSaveModel;

public class ChannelCreateViewModel extends ViewModel implements Observable {

    public final String groupSettingsFragmentTag = "groupSettingsFragmentTag",
                avatarSheetFragmentTag = "avatarSheetFragmentTag";

    public final SingleLiveEvent<Void> coverClickEvent = new SingleLiveEvent<>();

    private final ChatSaveModel chatSaveModel;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Bindable
    private Uri avatar;

    @Inject
    public ChannelCreateViewModel(ChatSaveModel chatSaveModel) {
        chatSaveModel.setType(ChatType.publicChannel.name());
        this.chatSaveModel = chatSaveModel;
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
