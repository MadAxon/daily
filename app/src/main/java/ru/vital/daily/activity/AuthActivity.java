package ru.vital.daily.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivityAuthBinding;
import ru.vital.daily.fragment.AuthFragment;
import ru.vital.daily.view.model.AuthViewModel;

public class AuthActivity extends BaseActivity<AuthViewModel, ActivityAuthBinding> {

    @Override
    protected AuthViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(AuthViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_auth;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new AuthFragment())
                    .commitNow();


    }
}
