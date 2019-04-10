package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.adapter.UserStoryAdapter;
import ru.vital.daily.listener.SingleLiveEvent;

public class FeedViewModel extends ViewModel {

    public SingleLiveEvent<Void> cameraClickedEvent = new SingleLiveEvent<>();

    public FeedViewModel(@NonNull Application application) {
        super(application);
    }

    public void onCameraClicked() {
        cameraClickedEvent.call();
    }
}
