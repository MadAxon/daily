package ru.vital.daily.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivityChatSelectorBinding;
import ru.vital.daily.fragment.HomeFragment;
import ru.vital.daily.repository.api.response.SocketResponse;
import ru.vital.daily.view.model.ChatSelectorViewModel;

import static ru.vital.daily.activity.ChatActivity.MESSAGE_IDS_EXTRA;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_READ;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_TYPE;
import static ru.vital.daily.enums.Operation.ACTION_PROFILE_ONLINE;

public class ChatSelectorActivity extends BaseActivity<ChatSelectorViewModel, ActivityChatSelectorBinding> {

    private BroadcastReceiver socketReceiver;

    @Override
    protected ChatSelectorViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(ChatSelectorViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_selector;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            replaceFragment(HomeFragment.newInstance());

        socketReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null)
                    switch (intent.getAction()) {
                        case ACTION_MESSAGE_SEND:
                            viewModel.setChatIdForUpdating(intent.getLongExtra(ChatActivity.CHAT_ID_EXTRA, 0));
                            break;
                        case ACTION_MESSAGE_TYPE:
                            viewModel.typingSocketEvent.setValue(intent.getLongExtra(ChatActivity.CHAT_ID_EXTRA, 0));
                            break;
                        case ACTION_MESSAGE_READ:
                            SocketResponse socketResponse = new SocketResponse();
                            socketResponse.setChatMessageIds(intent.getLongArrayExtra(MESSAGE_IDS_EXTRA));
                            socketResponse.setChatId(intent.getLongExtra(ChatActivity.CHAT_ID_EXTRA, 0));
                            viewModel.readMessageEvent.setValue(socketResponse);
                            break;
                        case ACTION_MESSAGE_DELETE:
                            viewModel.setChatIdForUpdating(intent.getLongExtra(ChatActivity.CHAT_ID_EXTRA, 0));
                            break;
                        case ACTION_PROFILE_ONLINE:

                            break;
                    }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(socketReceiver, new IntentFilter(ACTION_MESSAGE_SEND));
        registerReceiver(socketReceiver, new IntentFilter(ACTION_MESSAGE_TYPE));
        registerReceiver(socketReceiver, new IntentFilter(ACTION_MESSAGE_READ));
        registerReceiver(socketReceiver, new IntentFilter(ACTION_MESSAGE_DELETE));
        registerReceiver(socketReceiver, new IntentFilter(ACTION_PROFILE_ONLINE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(socketReceiver);
    }
}
