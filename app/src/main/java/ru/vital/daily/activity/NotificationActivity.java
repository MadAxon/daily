package ru.vital.daily.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.FeedAdapter;
import ru.vital.daily.adapter.NotificationAdapter;
import ru.vital.daily.databinding.ActivityNotificationBinding;
import ru.vital.daily.view.model.NotificationsViewModel;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends BaseActivity<NotificationsViewModel, ActivityNotificationBinding> {

    private boolean showMyself = true;

    @Override
    protected NotificationsViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(NotificationsViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.setAdapter(new NotificationAdapter());
        loadNotifications(showMyself);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notifications:
                if (showMyself)
                    item.setTitle(R.string.notifications_you);
                else
                    item.setTitle(R.string.notifications_following);
                showMyself = !showMyself;
                loadNotifications(showMyself);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNotifications(boolean showMyself) {
        dataBinding.getAdapter().clearAdapter();
        List<Object> feed = new ArrayList<>();
        for (int i = 0; i < (showMyself ? 3 : 6); i++)
            feed.add(new Object());
        dataBinding.getAdapter().updateItems(feed);
    }
}
