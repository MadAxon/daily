package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.view.model.item.FeedItemViewModel;

public class PostViewModel extends ViewModel {

    public FeedItemViewModel feedItemViewModel = new FeedItemViewModel();

    public SingleLiveEvent<Void> attachClickedEvent = new SingleLiveEvent<>(),
                                emoClickedEvent = new SingleLiveEvent<>(),
                                sendClickedEvent = new SingleLiveEvent<>();

    public PostViewModel(@NonNull Application application) {
        super(application);
    }


    public void onAttachClicked() {
        attachClickedEvent.call();
    }

    public void onEmoClicked() {
        emoClickedEvent.call();
    }

    public void onSendClicked() {
        sendClickedEvent.call();
    }
}
