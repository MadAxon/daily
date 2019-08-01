package ru.vital.daily.di.builder;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.activity.ChatCreateActivity;
import ru.vital.daily.activity.MainActivity;
import ru.vital.daily.activity.MediaEditorActivity;
import ru.vital.daily.activity.SettingsActivity;
import ru.vital.daily.di.builder.fragment.ChatCreateFragmentsBuilderModule;
import ru.vital.daily.di.builder.fragment.ChatFragmentBuilderModule;
import ru.vital.daily.di.builder.fragment.MainFragmentsBuilderModule;
import ru.vital.daily.di.builder.fragment.SettingsFragmentsBuilderModule;

@Module
public abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = MainFragmentsBuilderModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = SettingsFragmentsBuilderModule.class)
    abstract SettingsActivity bindSettingsActivity();

    @ContributesAndroidInjector(modules = ChatCreateFragmentsBuilderModule.class)
    abstract ChatCreateActivity bindChatCreateActivity();

    @ContributesAndroidInjector(modules = ChatFragmentBuilderModule.class)
    abstract ChatActivity bindChatActivity();

    @ContributesAndroidInjector
    abstract MediaEditorActivity bindMediaEditorActivity();

}
