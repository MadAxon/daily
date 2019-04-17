package ru.vital.daily.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.R;
import ru.vital.daily.BR;
import ru.vital.daily.databinding.ActivityMainBinding;
import ru.vital.daily.fragment.AccountFragment;
import ru.vital.daily.fragment.FeedFragment;
import ru.vital.daily.fragment.HomeFragment;
import ru.vital.daily.view.model.MainViewModel;

import android.view.MenuItem;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> implements BottomNavigationView.OnNavigationItemSelectedListener {

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
        dataBinding.navigation.setOnNavigationItemSelectedListener(this);
        dataBinding.navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .commit();
                return true;
            case R.id.navigation_feed:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new FeedFragment())
                        .commit();
                return true;
            case R.id.navigation_account:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AccountFragment())
                        .commit();
                return true;
        }
        return false;
    }


}
