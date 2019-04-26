package ru.vital.daily.view.model;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class ChatViewModel extends ViewModel {

    public SingleLiveEvent<Void> attachClickedEvent = new SingleLiveEvent<>(),
            emoClickedEvent = new SingleLiveEvent<>(),
            sendClickedEvent = new SingleLiveEvent<>();

    @Inject
    public ChatViewModel() {
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
