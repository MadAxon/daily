package ru.vital.daily.view.model;

import android.app.Application;
import android.util.Log;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.KeyRepository;

public class AuthViewModel extends ViewModel {

    public final String signInFragmentTag = "signInFragmentTag",
                    registerFragmentTag = "registerFragmentTag";

    public SingleLiveEvent<Void> openSignEmailEvent = new SingleLiveEvent<>(),
            openSignPhoneEvent = new SingleLiveEvent<>(),
            openRegisterEvent = new SingleLiveEvent<>();

    @Inject
    public AuthViewModel() {

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
