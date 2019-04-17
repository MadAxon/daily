package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentSettingsPersonalBinding;
import ru.vital.daily.enums.SettingsPersonalType;
import ru.vital.daily.view.model.SettingsPersonalViewModel;

public class SettingsPersonalFragment extends BaseFragment<SettingsPersonalViewModel, FragmentSettingsPersonalBinding> {

    private static final String SETTINGS_PERSONAL_TYPE_EXTRA = "SETTINGS_PERSONAL_TYPE_EXTRA";

    public static SettingsPersonalFragment newInstance(SettingsPersonalType settingsPersonalType) {

        Bundle args = new Bundle();
        args.putSerializable(SETTINGS_PERSONAL_TYPE_EXTRA, settingsPersonalType);
        SettingsPersonalFragment fragment = new SettingsPersonalFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SettingsPersonalViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(SettingsPersonalViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings_personal;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar(dataBinding.toolbar, true);

        if (getArguments() != null) {
            viewModel.currentType = (SettingsPersonalType) getArguments().getSerializable(SETTINGS_PERSONAL_TYPE_EXTRA);
            viewModel.title = viewModel.currentType.stringId;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_check, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
