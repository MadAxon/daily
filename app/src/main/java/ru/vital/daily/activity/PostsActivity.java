package ru.vital.daily.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.databinding.ActivityPostsBinding;
import ru.vital.daily.enums.ItemViewType;
import ru.vital.daily.util.GridItemDecoration;
import ru.vital.daily.view.model.PostsViewModel;

public class PostsActivity extends BaseActivity<PostsViewModel, ActivityPostsBinding> {

    private final GridItemDecoration gridItemDecoration = new GridItemDecoration();

    @Override
    protected PostsViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(PostsViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_posts;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.setFeedAdapter(new FeedAdapter());
        List<Object> feed = new ArrayList<>();
        feed.add(new Object());
        feed.add(new Object());
        feed.add(new Object());
        dataBinding.getFeedAdapter().updateItems(feed);

        viewModel.itemViewTypeClickedEvent.observe(this, aVoid -> {
            viewModel.notifyChanged(BR.currentViewType);
            if (dataBinding.getFeedAdapter().changeCurrentItemViewType(viewModel.currentViewType)) {
                switch (viewModel.currentViewType) {
                    case list:
                        dataBinding.recyclerViewFeed.setLayoutManager(new LinearLayoutManager(this));
                        dataBinding.recyclerViewFeed.removeItemDecoration(gridItemDecoration);
                        break;
                    case grid:
                        dataBinding.recyclerViewFeed.setLayoutManager(new GridLayoutManager(this, 2));
                        dataBinding.recyclerViewFeed.addItemDecoration(gridItemDecoration);
                }
                dataBinding.getFeedAdapter().notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_posts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_favorite:

                return true;
            case R.id.menu_refresh:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
