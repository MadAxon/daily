package ru.vital.daily.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.activity.AuthActivity;
import ru.vital.daily.activity.MainActivity;
import ru.vital.daily.activity.SettingsActivity;
import ru.vital.daily.di.builder.fragment.AuthFragmentsBuilderModule;
import ru.vital.daily.di.builder.fragment.MainFragmentsBuilderModule;
import ru.vital.daily.di.builder.fragment.SettingsFragmentsBuilderModule;

@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = AuthFragmentsBuilderModule.class)
    abstract AuthActivity bindAuthActivity();

    @ContributesAndroidInjector(modules = MainFragmentsBuilderModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = SettingsFragmentsBuilderModule.class)
    abstract SettingsActivity bindSettingsActivity();

}
