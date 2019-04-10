package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.enums.ItemViewType;
import ru.vital.daily.listener.SingleLiveEvent;

public class PostsViewModel extends ViewModel implements Observable {

    public ItemViewType listViewType = ItemViewType.list,
            gridViewType = ItemViewType.grid;

    @Bindable
    public ItemViewType currentViewType = listViewType;

    public SingleLiveEvent<Void> itemViewTypeClickedEvent = new SingleLiveEvent<>();

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    public PostsViewModel(@NonNull Application application) {
        super(application);
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

    public void onGridTypeClicked(ItemViewType itemViewType) {
        currentViewType = itemViewType;
        itemViewTypeClickedEvent.call();
    }

    public void onListTypeClicked(ItemViewType itemViewType) {
        currentViewType = itemViewType;
        itemViewTypeClickedEvent.call();
    }
}
