package ru.vital.daily.util;

import android.util.SparseArray;

import androidx.core.app.NotificationCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NotificationSparseArray {

    public final SparseArray<NotificationCompat.Builder> sparseArray = new SparseArray<>();

    @Inject
    public NotificationSparseArray() {
    }


}
