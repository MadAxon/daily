package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.AccountFragment;
import ru.vital.daily.fragment.FeedFragment;
import ru.vital.daily.fragment.HomeFragment;

@Module
public abstract class MainFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract HomeFragment bindHomeFragment();

    @ContributesAndroidInjector
    abstract FeedFragment bindFeedFragment();

    @ContributesAndroidInjector
    abstract AccountFragment bindAccountFragment();

}
