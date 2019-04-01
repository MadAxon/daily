package ru.vital.daily.view.model;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public abstract class ViewModel extends AndroidViewModel implements IViewModel {

    protected final String token;

    public ViewModel(@NonNull Application application) {
        super(application);
        token = "";
        //token = SharedPrefs.getInstance().getToken(application.getApplicationContext());
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {

    }
}
