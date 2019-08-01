package ru.vital.daily.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import dagger.android.AndroidInjection;
import ru.vital.daily.broadcast.ConnectivityBroadcast;
import ru.vital.daily.broadcast.MessageBroadcast;
import ru.vital.daily.enums.Operation;
import ru.vital.daily.util.SharedPrefs;

public class InternetService extends JobService implements ConnectivityBroadcast.ConnectivityListener {

    @Inject
    ConnectivityBroadcast connectivityBroadcast;

    @Inject
    SharedPrefs sharedPrefs;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        connectivityBroadcast.setConnectivityListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        registerReceiver(connectivityBroadcast, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        unregisterReceiver(connectivityBroadcast);
        return true;
    }

    @Override
    public void onNetworkChanged(boolean isOnline) {
        if (sharedPrefs.getOnline() != isOnline) {
            sharedPrefs.applyOnline(isOnline);
            Intent intent = new Intent(this, MessageBroadcast.class);
            intent.setAction(isOnline ? Operation.ACTION_INTERNET_ONLINE : Operation.ACTION_INTERNET_OFFLINE);
            sendBroadcast(intent);
        }
    }
}
