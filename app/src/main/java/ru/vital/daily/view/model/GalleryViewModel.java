package ru.vital.daily.view.model;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.databinding.PropertyChangeRegistry;

import ru.vital.daily.BR;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;

public class GalleryViewModel extends ViewModel implements Observable {

    public final SingleLiveEvent<Void> shareClickEvent = new SingleLiveEvent<>();

    public final ObservableField<String> title = new ObservableField<>();

    @Bindable
    private Media currentMedia;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    public Media getCurrentMedia() {
        return currentMedia;
    }

    public void setCurrentMedia(Media currentMedia) {
        this.currentMedia = currentMedia;
        callbacks.notifyChange(this, BR.currentMedia);
    }

    @Override
    public void addOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(Observable.OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public void onShareClicked() {
        shareClickEvent.call();
    }

}
