package ru.vital.daily.di.module;

import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ru.vital.daily.view.model.AccountViewModel;
import ru.vital.daily.view.model.AuthViewModel;
import ru.vital.daily.view.model.DailyViewModelFactory;
import ru.vital.daily.view.model.FeedViewModel;
import ru.vital.daily.view.model.HomeViewModel;
import ru.vital.daily.view.model.MainViewModel;
import ru.vital.daily.view.model.RegisterViewModel;
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
    abstract ViewModelProvider.Factory bindViewModelFactory(DailyViewModelFactory factory);

}
