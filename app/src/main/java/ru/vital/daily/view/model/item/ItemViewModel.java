package ru.vital.daily.view.model.item;

import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;
import ru.vital.daily.listener.SingleLiveEvent;

public abstract class ItemViewModel<M> extends ViewModel {

    private SingleLiveEvent<M> clickEvent;

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

    public void unbind() {

    }
}
