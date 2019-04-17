package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentGroupUsersBinding;
import ru.vital.daily.view.model.GroupUsersViewModel;

public class GroupUsersFragment extends BaseFragment<GroupUsersViewModel, FragmentGroupUsersBinding> {

    public static GroupUsersFragment newInstance() {

        Bundle args = new Bundle();

        GroupUsersFragment fragment = new GroupUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected GroupUsersViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(GroupUsersViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_users;
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

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
