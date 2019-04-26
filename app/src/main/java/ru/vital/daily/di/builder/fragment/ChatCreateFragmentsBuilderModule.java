package ru.vital.daily.di.builder.fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.fragment.ChannelCreateFragment;
import ru.vital.daily.fragment.ChannelSettingsFragment;
import ru.vital.daily.fragment.ChatCreateFragment;
import ru.vital.daily.fragment.ContactFragment;
import ru.vital.daily.fragment.GroupUsersFragment;

@Module
public abstract class ChatCreateFragmentsBuilderModule {

    @ContributesAndroidInjector
    abstract ChatCreateFragment bindChatCreateFragment();

    @ContributesAndroidInjector
    abstract ChannelCreateFragment bindGroupCreateFragment();

    @ContributesAndroidInjector
    abstract ChannelSettingsFragment bindGroupSettingsFragment();

    @ContributesAndroidInjector
    abstract GroupUsersFragment bindGroupUsersFragment();

    @ContributesAndroidInjector
    abstract ContactFragment bindContactFragment();

}
