package ru.vital.daily.activity;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivitySettingsBinding;
import ru.vital.daily.enums.SettingsPersonalType;
import ru.vital.daily.fragment.SettingsFragment;
import ru.vital.daily.fragment.SettingsPersonalFragment;
import ru.vital.daily.fragment.sheet.BaseSheetFragment;
import ru.vital.daily.view.model.SettingsViewModel;

public class SettingsActivity extends BaseActivity<SettingsViewModel, ActivitySettingsBinding> implements BaseSheetFragment.OnDismissListener {

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

    @Override
    public void onDismiss(String tag, int stringId) {
        if (tag.equals(viewModel.fragmentDescriptionTag)) {
            switch (stringId) {
                case R.string.common_change:
                    openFragment(SettingsPersonalFragment.newInstance(SettingsPersonalType.description));
                    break;
                case R.string.common_copy_link:

                    break;
            }
        } else if (tag.equals(viewModel.fragmentEmailTag)) {
            switch (stringId) {
                case R.string.common_change:
                    openFragment(SettingsPersonalFragment.newInstance(SettingsPersonalType.email));
                    break;
                case R.string.common_copy:

                    break;
            }
        } else if (tag.equals(viewModel.fragmentHistoryTag)) {
            switch (stringId) {
                case R.string.sheet_clean_search:

                    break;
            }
        } else if (tag.equals(viewModel.fragmentLoginTag)) {
            switch (stringId) {
                case R.string.common_change:
                    openFragment(SettingsPersonalFragment.newInstance(SettingsPersonalType.login));
                    break;
                case R.string.common_copy:

                    break;
            }
        } else if (tag.equals(viewModel.fragmentPhoneTag)) {
            switch (stringId) {
                case R.string.sheet_change_number:
                    openFragment(SettingsPersonalFragment.newInstance(SettingsPersonalType.phone));
                    break;
                case R.string.common_copy:

                    break;
            }
        }
    }

    private void copy(String text) {

    }
}
