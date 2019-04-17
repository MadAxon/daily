package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentGroupSettingsBinding;
import ru.vital.daily.view.model.GroupSettingsViewModel;

public class GroupSettingsFragment extends BaseFragment<GroupSettingsViewModel, FragmentGroupSettingsBinding> {

    public static GroupSettingsFragment newInstance() {

        Bundle args = new Bundle();

        GroupSettingsFragment fragment = new GroupSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected GroupSettingsViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(GroupSettingsViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_settings;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_check, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:
                openFragment(GroupUsersFragment.newInstance());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
