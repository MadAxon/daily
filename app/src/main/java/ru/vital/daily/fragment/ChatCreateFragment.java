package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.adapter.ContactAdapter;
import ru.vital.daily.databinding.FragmentChatCreateBinding;
import ru.vital.daily.view.model.ChatCreateViewModel;

public class ChatCreateFragment extends BaseFragment<ChatCreateViewModel, FragmentChatCreateBinding> {

    @Override
    protected ChatCreateViewModel onCreateViewModel() {
        return ViewModelProviders.of(getActivity()).get(ChatCreateViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_create;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.setAdapter(new ContactAdapter());
        viewModel.users.observe(this, users -> dataBinding.getAdapter().updateItems(users));
        viewModel.clearAdapterEvent.observe(this, aVoid -> dataBinding.getAdapter().clearAdapter());
        dataBinding.getAdapter().clickEvent.observe(this, user -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.MEMBER_ID_EXTRA, user.getId());
            startActivity(intent);
        });

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) dataBinding.recyclerView.getLayoutManager();
        dataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager.findLastVisibleItemPosition() == dataBinding.getAdapter().getItemCount() - 1
                    && viewModel.request.getPageIndex() * viewModel.request.getPageSize() + viewModel.request.getPageSize() == dataBinding.getAdapter().getItemCount())
                    viewModel.loadMore();
            }
        });

        viewModel.createGroupClickEvent.observe(this, aVoid -> {
            openFragment(ContactFragment.newInstance(true), viewModel.groupCreateFragmentTag);
        });
        viewModel.createChannelClickEvent.observe(this, aVoid -> {
            openFragment(new ChannelCreateFragment(), viewModel.groupCreateFragmentTag);
        });
    }
}
