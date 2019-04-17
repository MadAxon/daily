package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.BuildConfig;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentSettingsBinding;
import ru.vital.daily.fragment.sheet.SimpleSheetFragment;
import ru.vital.daily.view.model.SettingsViewModel;
import ru.vital.daily.view.model.SimpleSheetViewModel;

public class SettingsFragment extends BaseFragment<SettingsViewModel, FragmentSettingsBinding> {

    @Override
    protected SettingsViewModel onCreateViewModel() {
        return ViewModelProviders.of(getActivity()).get(SettingsViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);
        viewModel.versionName = BuildConfig.VERSION_NAME;

        viewModel.notificationClickedEvent.observe(this, aVoid -> {
            openFragment(new SettingsNotificationFragment());
        });
        viewModel.phoneClickedEvent.observe(this, aVoid -> {
            SimpleSheetFragment.newInstance("+7 (985) 822-81-18", new int[]{R.string.sheet_change_number, R.string.common_copy}).show(getChildFragmentManager(), viewModel.fragmentPhoneTag);
        });
        viewModel.historyClickedEvent.observe(this, aVoid -> {
            SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_garbage}, new int[]{R.string.sheet_clean_search}).show(getChildFragmentManager(), viewModel.fragmentHistoryTag);
        });
        viewModel.loginClickedEvent.observe(this, aVoid -> {
            SimpleSheetFragment.newInstance("qwery123", new int[]{R.string.common_change, R.string.common_copy}).show(getChildFragmentManager(), viewModel.fragmentLoginTag);
        });
        viewModel.descriptionClickedEvent.observe(this, aVoid -> {
            SimpleSheetFragment.newInstance(new int[]{R.string.common_change, R.string.common_copy_link}).show(getChildFragmentManager(), viewModel.fragmentDescriptionTag);
        });
        viewModel.conditionClickedEvent.observe(this, aVoid -> {});
        viewModel.policyClickedEvent.observe(this, aVoid -> {});
        viewModel.faqClickedEvent.observe(this, aVoid -> {});
        viewModel.privacyClickedEvent.observe(this, aVoid -> {});
        viewModel.originalClickedEvent.observe(this, aVoid -> {});
        viewModel.likedClickedEvent.observe(this, aVoid -> {});
        viewModel.emailClickedEvent.observe(this, aVoid -> {
            SimpleSheetFragment.newInstance("qwery123@awef.ru", new int[]{R.string.common_change, R.string.common_copy}).show(getChildFragmentManager(), viewModel.fragmentEmailTag);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings_log_out, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
