package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.HomeFragment;

@Module
public abstract class ChatSelectorFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract HomeFragment bindHomeFragment();

}
