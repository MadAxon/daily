package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;

public class SimpleSheetViewModel extends ViewModel {

    public String title;

    public SimpleSheetViewModel(@NonNull Application application) {
        super(application);
    }
}
