package ru.vital.daily.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivitySettingsBinding;
import ru.vital.daily.fragment.SettingsFragment;
import ru.vital.daily.view.model.SettingsViewModel;

public class SettingsActivity extends BaseActivity<SettingsViewModel, ActivitySettingsBinding> {

    @Override
    protected SettingsViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(SettingsViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commitNow();
    }

}
