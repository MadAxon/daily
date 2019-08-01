package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.AlbumFragment;
import ru.vital.daily.fragment.CameraFragment;
import ru.vital.daily.fragment.ContactFragment;
import ru.vital.daily.fragment.ContactFragmentList;
import ru.vital.daily.fragment.FileFragment;
import ru.vital.daily.fragment.LocationFragment;
import ru.vital.daily.fragment.MediaFragment;
import ru.vital.daily.fragment.MoneyFragment;

@Module
public abstract class ChatFragmentBuilderModule {

    @ContributesAndroidInjector
    abstract CameraFragment bindCameraFragment();

    @ContributesAndroidInjector
    abstract LocationFragment bindLocationFragment();

    @ContributesAndroidInjector
    abstract ContactFragmentList bindContactFragmentList();

    @ContributesAndroidInjector
    abstract FileFragment bindFileFragment();

    /*@ContributesAndroidInjector
    abstract MoneyFragment bindMoneyFragment();*/

    /*@ContributesAndroidInjector
    abstract MediaFragment bindMediaFragment();*/

    @ContributesAndroidInjector
    abstract AlbumFragment bindAlbumFragment();

}
