package ru.vital.daily.view.model;

import android.app.Application;
import android.widget.TextView;

import androidx.annotation.NonNull;
import ru.vital.daily.adapter.UserMessageAdapter;
import ru.vital.daily.listener.SingleLiveEvent;

public class HomeViewModel extends ViewModel {

    public SingleLiveEvent<Void> toolbarClickedEvent = new SingleLiveEvent<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void onToolbarClicked() {
        toolbarClickedEvent.call();
    }
}
