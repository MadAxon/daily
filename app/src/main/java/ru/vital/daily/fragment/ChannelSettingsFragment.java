package ru.vital.daily.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentChannelSettingsBinding;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.view.model.ChannelSettingsViewModel;

public class ChannelSettingsFragment extends BaseFragment<ChannelSettingsViewModel, FragmentChannelSettingsBinding> {

    private static final String CHAT_SAVE_MODEL_EXTRA = "CHAT_SAVE_MODEL_EXTRA",
                            COVER_PATH_EXTRA = "COVER_PATH_EXTRA";

    public static ChannelSettingsFragment newInstance(ChatSaveModel chatSaveModel, String path) {

        Bundle args = new Bundle();
        try {
            args.putString(CHAT_SAVE_MODEL_EXTRA, LoganSquare.serialize(chatSaveModel));
            args.putString(COVER_PATH_EXTRA, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ChannelSettingsFragment fragment = new ChannelSettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ChannelSettingsViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ChannelSettingsViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_channel_settings;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);
        if (getArguments() != null)
            try {
                viewModel.setChatSaveModel(LoganSquare.parse(getArguments().getString(CHAT_SAVE_MODEL_EXTRA), ChatSaveModel.class));
            } catch (IOException e) {
                e.printStackTrace();
                viewModel.setChatSaveModel(new ChatSaveModel());
            } finally {
                if (getArguments().getString(COVER_PATH_EXTRA) != null)
                    viewModel.setAvatar(Uri.parse(getArguments().getString(COVER_PATH_EXTRA)));
            }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_check_1, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_1:
               /* viewModel.createChannel(chatId -> {
                    openFragment(GroupUsersFragment.newInstance(chatId), viewModel.groupUsersFragmentTag);
                });*/
                openFragment(ContactFragment.newInstance(0), viewModel.contactFragmentTag);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
