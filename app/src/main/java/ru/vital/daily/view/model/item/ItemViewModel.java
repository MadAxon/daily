package ru.vital.daily.view.model.item;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;
import ru.vital.daily.listener.SingleLiveEvent;

public abstract class ItemViewModel<M> extends ViewModel implements Observable {

    private SingleLiveEvent<M> clickEvent;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    public M item;

    public void setItem(M item) {
        this.item = item;
    }

    public void setClickEvent(SingleLiveEvent<M> clickEvent) {
        this.clickEvent = clickEvent;
    }

    public void onItemClicked() {
        if (clickEvent != null)
            clickEvent.setValue(item);
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public void notifyChange() {
        callbacks.notifyChange(this, 0);
    }

    public void notifyChange(int fieldId) {
        callbacks.notifyChange(this, fieldId);
    }
}
