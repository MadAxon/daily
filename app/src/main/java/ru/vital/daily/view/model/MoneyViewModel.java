package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class MoneyViewModel extends ViewModel {

    public SingleLiveEvent<Void> sendMoneyClickedEvent = new SingleLiveEvent<>();

    public void onSendMoneyClicked() {
        sendMoneyClickedEvent.call();
    }
}
