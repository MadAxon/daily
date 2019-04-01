package ru.vital.daily.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.R;
import ru.vital.daily.BR;
import ru.vital.daily.databinding.ActivityMainBinding;
import ru.vital.daily.fragment.HomeFragment;
import ru.vital.daily.view.model.MainViewModel;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Fragment homeFragment, accountFragment;

    @Override
    public MainViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(MainViewModel.class);
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                if (homeFragment == null)
                    prepareFragmentTransaction(homeFragment = new HomeFragment())
                            .commit();
                else prepareFragmentTransaction(homeFragment)
                        .addToBackStack("homeFragment")
                        .commit();
                return true;
            case R.id.navigation_history:

                return true;
            case R.id.navigation_account:
                prepareFragmentTransaction(accountFragment == null ? accountFragment = new HomeFragment() : accountFragment)
                        .addToBackStack("accountFragment")
                        .commit();
                return true;
        }
        return false;
    }

    private FragmentTransaction prepareFragmentTransaction(Fragment fragment) {
        return getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment);
    }
}
