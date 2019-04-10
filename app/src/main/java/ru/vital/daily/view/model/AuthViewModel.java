package ru.vital.daily.view.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class AuthViewModel extends ViewModel {

    public SingleLiveEvent<Void> openSignEmailEvent = new SingleLiveEvent<>(),
            openSignPhoneEvent = new SingleLiveEvent<>(),
            openRegisterEvent = new SingleLiveEvent<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
    }

    public void openSignInEmail() {
        openSignEmailEvent.call();
    }

    public void openSignInPhone() {
        openSignPhoneEvent.call();
    }

    public void openRegister() {
        openRegisterEvent.call();
    }

    public void bottomClicked() {
        Log.i("my_logs", "bottom clicked");
    }
}
