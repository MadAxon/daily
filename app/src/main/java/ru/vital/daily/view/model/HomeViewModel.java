package ru.vital.daily.view.model;

import android.app.Application;
import android.widget.TextView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import ru.vital.daily.adapter.UserMessageAdapter;
import ru.vital.daily.listener.SingleLiveEvent;

public class HomeViewModel extends ViewModel {

    public SingleLiveEvent<Void> toolbarClickedEvent = new SingleLiveEvent<>();

    @Inject
    public HomeViewModel() {

    }

    public void onToolbarClicked() {
        toolbarClickedEvent.call();
    }
}
