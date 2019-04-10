package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;

public class SignInViewModel extends ViewModel {

    public boolean isPhoneSignIn;

    public SignInViewModel(@NonNull Application application) {
        super(application);
    }


}
