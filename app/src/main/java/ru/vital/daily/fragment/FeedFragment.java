package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.NotificationActivity;
import ru.vital.daily.activity.PostActivity;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.adapter.UserStoryAdapter;
import ru.vital.daily.databinding.FragmentFeedBinding;
import ru.vital.daily.fragment.sheet.ChatSheetFragment;
import ru.vital.daily.view.model.FeedViewModel;

public class FeedFragment extends BaseFragment<FeedViewModel, FragmentFeedBinding> {

    @Override
    protected FeedViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(FeedViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_feed;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, false);
        viewModel.cameraClickedEvent.observe(this, aVoid -> {

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
        dataBinding.getFeedAdapter().clickEvent.observe(this, o -> {
            startActivity(new Intent(getContext(), PostActivity.class));
        });

        dataBinding.getFeedAdapter().userClickedEvent.observe(this, aVoid -> {
            new ChatSheetFragment().show(getActivity().getSupportFragmentManager(), null);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notification:
                startActivity(new Intent(getContext(), NotificationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
