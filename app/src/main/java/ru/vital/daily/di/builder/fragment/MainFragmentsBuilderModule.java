package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.AccountFragment;
import ru.vital.daily.fragment.AuthFragment;
import ru.vital.daily.fragment.ConfirmationFragment;
import ru.vital.daily.fragment.FeedFragment;
import ru.vital.daily.fragment.HomeFragment;
import ru.vital.daily.fragment.RegisterFragment;
import ru.vital.daily.fragment.SignInFragment;

@Module
public abstract class MainFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract HomeFragment bindHomeFragment();

    @ContributesAndroidInjector
    abstract FeedFragment bindFeedFragment();

    @ContributesAndroidInjector
    abstract AccountFragment bindAccountFragment();

    @ContributesAndroidInjector
    abstract AuthFragment bindAuthFragment();

    @ContributesAndroidInjector
    abstract RegisterFragment bindRegisterFragment();

    @ContributesAndroidInjector
    abstract SignInFragment bindSignInFragment();

    @ContributesAndroidInjector
    abstract ConfirmationFragment bindConfirmationFragment();

}
