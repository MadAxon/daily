package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.adapter.ContactAdapter;
import ru.vital.daily.databinding.FragmentContactBinding;
import ru.vital.daily.view.model.ContactViewModel;

public class ContactFragment extends BaseFragment<ContactViewModel, FragmentContactBinding> {

    private static final String CHAT_ID_EXTRA = "CHAT_ID_EXTRA",
                        GROUP_CREATING_EXTRA = "GROUP_CREATING_EXTRA";

    private boolean shouldCreateGroup;

    @Nullable
    private MenuItem checkMenuItem;

    public static ContactFragment newInstance(long chatId) {

        Bundle args = new Bundle();
        args.putLong(CHAT_ID_EXTRA, chatId);
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ContactFragment newInstance(boolean shouldCreateGroup) {

        Bundle args = new Bundle();
        args.putBoolean(GROUP_CREATING_EXTRA, shouldCreateGroup);
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ContactViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ContactViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        if (getArguments() != null) {
            viewModel.getRequest().setId(getArguments().getLong(CHAT_ID_EXTRA));
            if (viewModel.getRequest().getId() != 0) {
                dataBinding.title.setText(R.string.chat_add_members);
            }
            if (shouldCreateGroup = getArguments().getBoolean(GROUP_CREATING_EXTRA))
                dataBinding.title.setText(R.string.chat_add_members);
        }

        dataBinding.setAdapter(new ContactAdapter());

        viewModel.users.observe(this, users -> dataBinding.getAdapter().updateItems(users));
        viewModel.clearAdapterEvent.observe(this, aVoid -> dataBinding.getAdapter().clearAdapter());
        dataBinding.getAdapter().clickEvent.observe(this, user -> {
            dataBinding.getAdapter().onItemSelected(user);
            if (checkMenuItem != null) {
                if (dataBinding.getAdapter().hasNoSelections())
                    checkMenuItem.setVisible(false);
                else checkMenuItem.setVisible(true);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();

        if (shouldCreateGroup) {
            inflater.inflate(R.menu.menu_check_1, menu);
            checkMenuItem = menu.findItem(R.id.menu_check_1);
            checkMenuItem.setVisible(false);
        } else {
            inflater.inflate(R.menu.menu_check_2, menu);
            menu.findItem(R.id.menu_check_2);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_1:
                openFragment(GroupUsersFragment.newInstance(dataBinding.getAdapter().getSelectedUsers()), viewModel.groupUsersFragmentTag);
                return true;
            case R.id.menu_check_2:
                viewModel.addMembersToChat(aVoid -> {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_ID_EXTRA, viewModel.getRequest().getId());
                    startActivity(intent);
                }, dataBinding.getAdapter().getSelectedIds());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
