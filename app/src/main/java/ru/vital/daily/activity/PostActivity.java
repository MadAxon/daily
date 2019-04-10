package ru.vital.daily.activity;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.UserCommentAdapter;
import ru.vital.daily.databinding.ActivityPostBinding;
import ru.vital.daily.view.model.PostViewModel;

public class PostActivity extends BaseActivity<PostViewModel, ActivityPostBinding> {

    @Override
    protected PostViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(PostViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.setAdapter(new UserCommentAdapter());
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        list.add(new Object());
        dataBinding.getAdapter().updateItems(list);

    }
}
