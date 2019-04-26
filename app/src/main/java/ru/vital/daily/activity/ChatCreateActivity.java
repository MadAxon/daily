package ru.vital.daily.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivityChatCreateBinding;
import ru.vital.daily.fragment.ChatCreateFragment;
import ru.vital.daily.view.model.ChatCreateViewModel;

public class ChatCreateActivity extends BaseActivity<ChatCreateViewModel, ActivityChatCreateBinding> {

    @Override
    protected ChatCreateViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ChatCreateViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_create;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null)
            replaceFragment(new ChatCreateFragment(), viewModel.chatCreateFragmentTag);
    }
}
