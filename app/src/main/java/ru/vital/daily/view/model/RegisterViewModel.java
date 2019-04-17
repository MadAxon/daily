package ru.vital.daily.view.model;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class RegisterViewModel extends ViewModel {

    public SingleLiveEvent<Void> goBackEvent = new SingleLiveEvent<>(),
                                countryClickedEvent = new SingleLiveEvent<>();

    @Inject
    public RegisterViewModel() {

    }

    public void goBack() {
        goBackEvent.call();
    }

    public void onCountryClick() {
        countryClickedEvent.call();
    }
}
