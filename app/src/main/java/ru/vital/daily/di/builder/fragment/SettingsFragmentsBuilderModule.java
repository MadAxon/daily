package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.SettingsFragment;
import ru.vital.daily.fragment.SettingsPersonalFragment;

@Module
public abstract class SettingsFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract SettingsPersonalFragment bindSettingsPersonalFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment bindSettingsFragment();

}
