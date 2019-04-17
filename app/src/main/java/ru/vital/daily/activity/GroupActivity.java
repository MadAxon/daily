package ru.vital.daily.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivityGroupBinding;
import ru.vital.daily.fragment.AuthFragment;
import ru.vital.daily.fragment.GroupCreateFragment;
import ru.vital.daily.view.model.GroupViewModel;

import android.os.Bundle;

public class GroupActivity extends BaseActivity<GroupViewModel, ActivityGroupBinding> {

    @Override
    protected GroupViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(GroupViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group;
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
                    .replace(R.id.container, new GroupCreateFragment())
                    .commitNow();
    }
}
