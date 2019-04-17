package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class LocationViewModel extends ViewModel {

    public SingleLiveEvent<Void> shareLiveClickEvent = new SingleLiveEvent<>(),
                                sendCurrentClickEvent = new SingleLiveEvent<>();

    public void onShareLiveClicked() {
        shareLiveClickEvent.call();
    }

    public void onSendCurrentClicked() {
        sendCurrentClickEvent.call();
    }
}
