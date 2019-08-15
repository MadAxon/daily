package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.util.MediaProgressHelper;

public class ChatSheetViewModel extends ViewModel {

    public SingleLiveEvent<Integer> cameraSheetClickEvent = new SingleLiveEvent<>();

    private final MediaProgressHelper mediaProgressHelper;

    @Inject
    public ChatSheetViewModel(MediaProgressHelper mediaProgressHelper) {
        this.mediaProgressHelper = mediaProgressHelper;
    }

    public MediaProgressHelper getMediaProgressHelper() {
        return mediaProgressHelper;
    }
}
