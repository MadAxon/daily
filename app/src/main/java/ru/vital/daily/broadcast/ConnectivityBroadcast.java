package ru.vital.daily.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.MutableLiveData;

@Singleton
public class ConnectivityBroadcast extends BroadcastReceiver {

    public final MutableLiveData<Boolean> isOnline = new MutableLiveData<>();

    @Inject
    ConnectivityManager connectivityManager;

    private ConnectivityListener connectivityListener;

    @Inject
    public ConnectivityBroadcast() {
        connectivityListener = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            boolean isOnline = networkInfo != null && networkInfo.isConnected();
            if (this.isOnline.getValue() == null && !isOnline) this.isOnline.setValue(isOnline);
            else if (this.isOnline.getValue() != null && isOnline != this.isOnline.getValue()) this.isOnline.setValue(isOnline);
            if (connectivityListener != null) connectivityListener.onNetworkChanged(isOnline);
        }
    }

    public ConnectivityListener getConnectivityListener() {
        return connectivityListener;
    }

    public void setConnectivityListener(ConnectivityListener connectivityListener) {
        this.connectivityListener = connectivityListener;
    }

    public interface ConnectivityListener {
        void onNetworkChanged(boolean isOnline);
    }
}
