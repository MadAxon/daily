package ru.vital.daily.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.R;
import ru.vital.daily.BR;
import ru.vital.daily.databinding.ActivityMainBinding;
import ru.vital.daily.di.FragmentInjectable;
import ru.vital.daily.fragment.AccountFragment;
import ru.vital.daily.fragment.AuthFragment;
import ru.vital.daily.fragment.FeedFragment;
import ru.vital.daily.fragment.HomeFragment;
import ru.vital.daily.fragment.RegisterFragment;
import ru.vital.daily.repository.api.AccessInterceptor;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.view.model.MainViewModel;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    AccessInterceptor accessInterceptor;

    @Override
    public MainViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            viewModel.mainShowEvent.observe(this, aVoid -> {
                dataBinding.navigation.setOnNavigationItemSelectedListener(this);
                dataBinding.navigation.setSelectedItemId(R.id.navigation_home);
                dataBinding.navigation.setVisibility(View.VISIBLE);
            });

            DisposableProvider.getDisposableItem(viewModel.getKeys(),
                    key -> {
                        accessInterceptor.setAccessKey(key.getAccessKey());
                        if (key.getUserId() != 0)
                            viewModel.mainShowEvent.call();
                        else
                            replaceFragment(new RegisterFragment(), viewModel.registerFragmentTag);
                    },
                    throwable -> {
                        replaceFragment(new AuthFragment(), viewModel.authFragmentTag);
                    });
                            /*new DisposableSingleObserver<Key>() {
                        @Override
                        public void onSuccess(Key key) {
                            accessInterceptor.setAccessKey(key.getAccessKey());
                            if (key.getUserId() != 0)
                                viewModel.mainShowEvent.call();
                            else
                                replaceFragment(new RegisterFragment(), viewModel.registerFragmentTag);
                        }

                        @Override
                        public void onError(Throwable e) {
                            replaceFragment(new AuthFragment(), viewModel.authFragmentTag);
                        }
                    }*/
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                replaceFragment(new HomeFragment(), viewModel.homeFragmentTag);
                return true;
            case R.id.navigation_feed:
                replaceFragment(new FeedFragment(), viewModel.feedFragmentTag);
                return true;
            case R.id.navigation_account:
                replaceFragment(new AccountFragment(), viewModel.accountFragmentTag);
                return true;
        }
        return false;
    }


}
