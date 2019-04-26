package ru.vital.daily.di.module;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.vital.daily.view.model.AccountViewModel;
import ru.vital.daily.view.model.AuthViewModel;
import ru.vital.daily.view.model.ChannelCreateViewModel;
import ru.vital.daily.view.model.ChannelSettingsViewModel;
import ru.vital.daily.view.model.ChatCreateViewModel;
import ru.vital.daily.view.model.ChatViewModel;
import ru.vital.daily.view.model.ConfirmationViewModel;
import ru.vital.daily.view.model.ContactViewModel;
import ru.vital.daily.view.model.DailyViewModelFactory;
import ru.vital.daily.view.model.FeedViewModel;
import ru.vital.daily.view.model.GroupUsersViewModel;
import ru.vital.daily.view.model.HomeViewModel;
import ru.vital.daily.view.model.MainViewModel;
import ru.vital.daily.view.model.RegisterViewModel;
import ru.vital.daily.view.model.SignInViewModel;
import ru.vital.daily.view.model.ViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel.class)
    abstract ViewModel bindAuthViewModel(AuthViewModel authViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel.class)
    abstract ViewModel bindAccountViewModel(AccountViewModel accountViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel.class)
    abstract ViewModel bindFeedViewModel(FeedViewModel feedViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel.class)
    abstract ViewModel bindSignInViewModel(SignInViewModel signInViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RegisterViewModel.class)
    abstract ViewModel bindRegisterViewModel(RegisterViewModel registerViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ConfirmationViewModel.class)
    abstract ViewModel bindConfirmationViewModel(ConfirmationViewModel confirmationViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChatCreateViewModel.class)
    abstract ViewModel bindChatCreateViewModel(ChatCreateViewModel chatCreateViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChannelCreateViewModel.class)
    abstract ViewModel bindGroupCreateViewModel(ChannelCreateViewModel groupCreateViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChannelSettingsViewModel.class)
    abstract ViewModel bindGroupSettingsViewModel(ChannelSettingsViewModel groupSettingsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GroupUsersViewModel.class)
    abstract ViewModel bindGroupUsersViewModel(GroupUsersViewModel groupUsersViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ContactViewModel.class)
    abstract ViewModel bindContactViewModel(ContactViewModel contactViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel.class)
    abstract ViewModel bindChatViewModel(ChatViewModel chatViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(DailyViewModelFactory factory);

}
