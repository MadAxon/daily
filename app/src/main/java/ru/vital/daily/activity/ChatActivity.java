package ru.vital.daily.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.ChatAdapter;
import ru.vital.daily.databinding.ActivityChatBinding;
import ru.vital.daily.view.model.ChatViewModel;

public class ChatActivity extends BaseActivity<ChatViewModel, ActivityChatBinding> implements ActionMode.Callback {

    private ActionMode actionMode;

    @Override
    protected ChatViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(ChatViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.setAdapter(new ChatAdapter());
        dataBinding.getAdapter().clickEvent.observe(this, o -> {

        });
        dataBinding.getAdapter().longClickEvent.observe(this, o -> {
            if (actionMode == null)
                actionMode = startSupportActionMode(this);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_call:

                return true;
            case R.id.menu_chat_more:

                return true;
            case R.id.menu_chat_notifications:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_message_copy:

                return true;
            case R.id.menu_message_delete:

                return true;
            case R.id.menu_message_resend:

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }
}
