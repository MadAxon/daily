package ru.vital.daily.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.ContactAdapter;
import ru.vital.daily.databinding.ActivityGroupInfoBinding;
import ru.vital.daily.view.model.GroupInfoViewModel;

public class GroupInfoActivity extends BaseActivity<GroupInfoViewModel, ActivityGroupInfoBinding> {

    @Override
    protected GroupInfoViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(GroupInfoViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_info;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar(dataBinding.toolbar, true);
        dataBinding.setAdapter(new ContactAdapter());
    }
}
