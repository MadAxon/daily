package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.AuthFragment;
import ru.vital.daily.fragment.RegisterFragment;

@Module
public abstract class AuthFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract AuthFragment bindAuthFragment();

    @ContributesAndroidInjector
    abstract RegisterFragment bindRegisterFragment();

}
