package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentSettingsNotificationBinding;
import ru.vital.daily.view.model.SettingsNotificationViewModel;

public class SettingsNotificationFragment extends BaseFragment<SettingsNotificationViewModel, FragmentSettingsNotificationBinding> {
    @Override
    protected SettingsNotificationViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(SettingsNotificationViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings_notification;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar(dataBinding.toolbar, true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
}
