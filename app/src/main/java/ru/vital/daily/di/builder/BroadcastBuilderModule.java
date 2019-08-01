package ru.vital.daily.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.broadcast.MessageBroadcast;

@Module
public abstract class BroadcastBuilderModule {

    @ContributesAndroidInjector
    abstract MessageBroadcast bindMessageBroadcast();

}
