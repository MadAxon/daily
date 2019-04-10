package ru.vital.daily.view.model.item;

import ru.vital.daily.listener.SingleLiveEvent;

public class FeedItemViewModel extends ItemViewModel<Object> {

    public SingleLiveEvent<Void> userClickedEvent;

    @Override
    public void setItem(Object item) {

    }

    public void onUserClicked() {
        userClickedEvent.call();
    }

    public void onMoreClicked() {

    }

    public void onMediaClicked() {

    }

    public void onFavoriteClicked() {

    }

    public void onMessageClicked() {

    }

    public void onShareClicked() {

    }

    public void onBookmarkClicked() {

    }

    public void onShowCommentsClicked() {

    }
}
