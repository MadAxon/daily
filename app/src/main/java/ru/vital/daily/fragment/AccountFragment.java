package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.SettingsActivity;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.adapter.UserStoryAdapter;
import ru.vital.daily.databinding.FragmentAccountBinding;
import ru.vital.daily.util.GridItemDoubleDecoration;
import ru.vital.daily.view.model.AccountViewModel;

public class AccountFragment extends BaseFragment<AccountViewModel, FragmentAccountBinding> {

    private final GridItemDoubleDecoration gridItemDecoration = new GridItemDoubleDecoration();

    @Override
    protected AccountViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(AccountViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, false);
        viewModel.focusClickedEvent.observe(this, aVoid -> {

        });
        viewModel.toolbarClickedEvent.observe(this, aVoid -> {

        });
        viewModel.avatarClickedEvent.observe(this, aVoid -> {
            Log.i("my_logs", "avatar clicked");
        });
        viewModel.coverClickedEvent.observe(this, aVoid -> {

        });
        viewModel.editClickedEvent.observe(this, aVoid -> {

        });
        viewModel.subscriptionsClickedEvent.observe(this, aVoid -> {

        });
        viewModel.subscriversClickedEvent.observe(this, aVoid -> {

        });
        viewModel.writeClickedEvent.observe(this, aVoid -> {

        });
        viewModel.itemViewTypeClickedEvent.observe(this, aVoid -> {
            viewModel.notifyChanged(BR.currentViewType);
            if (dataBinding.getFeedAdapter().changeCurrentItemViewType(viewModel.currentViewType)) {
                switch (viewModel.currentViewType) {
                    case list:
                        dataBinding.recyclerViewFeed.setLayoutManager(new LinearLayoutManager(getContext()));
                        dataBinding.recyclerViewFeed.removeItemDecoration(gridItemDecoration);
                        break;
                    case grid:
                        dataBinding.recyclerViewFeed.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        dataBinding.recyclerViewFeed.addItemDecoration(gridItemDecoration);
                }
                dataBinding.getFeedAdapter().notifyDataSetChanged();
            }
        });

        dataBinding.setUserStoryAdapter(new UserStoryAdapter());
        List<Object> list = new ArrayList<>();
        list.add(new Object());
        list.add(new Object());
        list.add(new Object());
        dataBinding.getUserStoryAdapter().updateItems(list);

        dataBinding.setFeedAdapter(new FeedAdapter());
        List<Object> feed = new ArrayList<>();
        feed.add(new Object());
        feed.add(new Object());
        feed.add(new Object());
        dataBinding.getFeedAdapter().updateItems(feed);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
