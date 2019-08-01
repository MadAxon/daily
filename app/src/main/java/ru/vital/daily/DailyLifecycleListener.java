package ru.vital.daily;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vital.daily.repository.api.DailySocket;

@Singleton
public class DailyLifecycleListener implements LifecycleObserver {

    @Inject
    DailySocket dailySocket;

    @Inject
    public DailyLifecycleListener() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        dailySocket.reconnect();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onMoveToBackground() {
        dailySocket.disconnect();
    }

}
