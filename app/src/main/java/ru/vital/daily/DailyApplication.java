package ru.vital.daily;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.crashlytics.android.Crashlytics;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;
import io.fabric.sdk.android.Fabric;
import ru.vital.daily.di.AppInjector;
import ru.vital.daily.di.component.AppComponent;
import ru.vital.daily.di.component.DaggerAppComponent;
import ru.vital.daily.repository.api.DailySocket;

public class DailyApplication extends Application implements HasActivityInjector, HasServiceInjector, HasBroadcastReceiverInjector {

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> dispatchingBroadcastInjector;

    @Inject
    DailyLifecycleListener dailyLifecycleListener;

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);
        AppInjector.init(this);
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {}
            });
        } catch (FFmpegNotSupportedException e) {
            /*new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setIcon(android.R.drawable.stat_notify_error)
                    .setTitle(R.string.ffmpeg_support_error_title)
                    .setMessage(R.string.ffmpeg_support_error_summary)
                    .setPositiveButton(android.R.string.ok, null);*/
        }

        ProcessLifecycleOwner.get().getLifecycle().addObserver(dailyLifecycleListener);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return dispatchingBroadcastInjector;
    }
}
