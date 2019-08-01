package ru.vital.daily.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.service.DownloadService;
import ru.vital.daily.service.InternetService;
import ru.vital.daily.service.MessageService;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract MessageService bindMessageService();

    @ContributesAndroidInjector
    abstract InternetService bindInternetService();

    @ContributesAndroidInjector
    abstract DownloadService bindDownloadService();

}
