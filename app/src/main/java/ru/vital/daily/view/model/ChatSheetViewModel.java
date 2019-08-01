package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class ChatSheetViewModel extends ViewModel {

    public SingleLiveEvent<Integer> cameraSheetClickEvent = new SingleLiveEvent<>();

}
