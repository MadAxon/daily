package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class RegisterViewModel extends ViewModel {

    public SingleLiveEvent<Void> goBackEvent = new SingleLiveEvent<>(),
                                countryClickedEvent = new SingleLiveEvent<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public void goBack() {
        goBackEvent.call();
    }

    public void onCountryClick() {
        countryClickedEvent.call();
    }
}
