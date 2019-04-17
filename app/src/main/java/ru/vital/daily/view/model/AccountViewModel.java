package ru.vital.daily.view.model;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.fragment.app.FragmentPagerAdapter;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.adapter.UserStoryAdapter;
import ru.vital.daily.enums.ItemViewType;
import ru.vital.daily.listener.SingleLiveEvent;

public class AccountViewModel extends ViewModel implements Observable {

    public SingleLiveEvent<Void> focusClickedEvent = new SingleLiveEvent<>(),
            toolbarClickedEvent = new SingleLiveEvent<>(),
            avatarClickedEvent = new SingleLiveEvent<>(),
            coverClickedEvent = new SingleLiveEvent<>(),
            writeClickedEvent = new SingleLiveEvent<>(),
            editClickedEvent = new SingleLiveEvent<>(),
            subscriversClickedEvent = new SingleLiveEvent<>(),
            subscriptionsClickedEvent = new SingleLiveEvent<>(),
            itemViewTypeClickedEvent = new SingleLiveEvent<>();

    public ItemViewType listViewType = ItemViewType.list,
                    gridViewType = ItemViewType.grid;

    @Bindable
    public ItemViewType currentViewType = listViewType;

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Inject
    public AccountViewModel() {

    }

    public void onFocusClicked() {
        focusClickedEvent.call();
    }

    public void onToolbarClicked() {
        toolbarClickedEvent.call();
    }

    public void onAvatarClicked() {
        avatarClickedEvent.call();
    }

    public void onCoverClicked() {
        coverClickedEvent.call();
    }

    public void onWriteClicked() {
        writeClickedEvent.call();
    }

    public void onEditClicked() {
        editClickedEvent.call();
    }

    public void onSubscribersClicked() {
        subscriversClickedEvent.call();
    }

    public void onSubscriptionsClicked() {
        subscriptionsClickedEvent.call();
    }

    public void onGridTypeClicked(ItemViewType itemViewType) {
        currentViewType = itemViewType;
        itemViewTypeClickedEvent.call();
    }

    public void onListTypeClicked(ItemViewType itemViewType) {
        currentViewType = itemViewType;
        itemViewTypeClickedEvent.call();
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

}
