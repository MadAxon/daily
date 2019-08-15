package ru.vital.daily.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.broadcast.DownloadBroadcast;
import ru.vital.daily.broadcast.MessageBroadcast;
import ru.vital.daily.broadcast.NotificationBroadcast;

@Module
public abstract class BroadcastBuilderModule {

    @ContributesAndroidInjector
    abstract MessageBroadcast bindMessageBroadcast();

    @ContributesAndroidInjector
    abstract DownloadBroadcast bindDownloadBroadcast();

    @ContributesAndroidInjector
    abstract NotificationBroadcast bindNotificationBroadcast();

}
