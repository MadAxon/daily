package ru.vital.daily.view.model;

import android.content.Intent;

public interface IViewModel {

    void onCreate();

    void onResume();

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onDestroy();
}
