package ru.vital.daily.view.model;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.PropertyChangeRegistry;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.vital.daily.BR;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.util.SelectedMedias;

public class MediaEditorViewModel extends ViewModel implements Observable {

    public final SingleLiveEvent<Void> descriptionClickEvent = new SingleLiveEvent<>(),
                                    doneClickEvent = new SingleLiveEvent<>(),
                                    sendClickEvent = new SingleLiveEvent<>();

    public final ObservableBoolean hasEdit = new ObservableBoolean(false),
                                selected = new ObservableBoolean();

    private final SelectedMedias selectedMedias;

    @Bindable
    private Media currentMedia;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Inject
    public MediaEditorViewModel(SelectedMedias selectedMedias) {
        this.selectedMedias = selectedMedias;
    }

    public void onDescriptionClick() {
        descriptionClickEvent.call();
    }

    public void onDoneClick() {
        doneClickEvent.call();
    }

    public void onSendClick() {
        sendClickEvent.call();
    }

    public void onMediaCheck(boolean selected) {
        if (selected != currentMedia.getSelected())
            selectedMedias.checkMedia(currentMedia);
    }

    public Media getCurrentMedia() {
        return currentMedia;
    }

    public void setCurrentMedia(Media currentMedia) {
        this.currentMedia = currentMedia;
        callbacks.notifyChange(this, BR.currentMedia);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public SelectedMedias getSelectedMedias() {
        return selectedMedias;
    }
}
