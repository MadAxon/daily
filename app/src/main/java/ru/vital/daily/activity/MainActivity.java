package ru.vital.daily.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.lifecycle.ViewModelProviders;

import ru.vital.daily.R;
import ru.vital.daily.BR;
import ru.vital.daily.databinding.ActivityMainBinding;
import ru.vital.daily.fragment.AccountFragment;
import ru.vital.daily.fragment.AuthFragment;
import ru.vital.daily.fragment.FeedFragment;
import ru.vital.daily.fragment.HomeFragment;
import ru.vital.daily.fragment.RegisterFragment;
import ru.vital.daily.repository.api.AccessInterceptor;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.api.response.SocketResponse;
import ru.vital.daily.service.InternetService;
import ru.vital.daily.service.NotificationService;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.StaticData;
import ru.vital.daily.view.model.MainViewModel;

import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import static ru.vital.daily.activity.ChatActivity.MESSAGE_IDS_EXTRA;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_DELETE;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_READ;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_SEND;
import static ru.vital.daily.enums.Operation.ACTION_MESSAGE_TYPE;
import static ru.vital.daily.enums.Operation.ACTION_PROFILE_ONLINE;

public class MainActivity extends BaseActivity<MainViewModel, ActivityMainBinding> implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Inject
    AccessInterceptor accessInterceptor;

    @Inject
    DailySocket dailySocket;

    @Inject
    NotificationService notificationService;

    private BroadcastReceiver socketReceiver;

    @Override
    public MainViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel.isOnline = connectivityBroadcast.isOnline;

        if (savedInstanceState == null) {
            viewModel.mainShowEvent.observe(this, aVoid -> {
                dataBinding.navigation.setOnNavigationItemSelectedListener(this);
                dataBinding.navigation.setSelectedItemId(R.id.navigation_home);
                dataBinding.navigation.setVisibility(View.VISIBLE);
            });

            DisposableProvider.getDisposableItem(viewModel.getKeys(),
                    key -> {
                        dailySocket.connect(key.getAccessKey());
                        accessInterceptor.setAccessKey(key.getAccessKey());
                        notificationService.subscribe(key.getNotificationTopic());
                        if (key.getUserId() != 0) {
                            DisposableProvider.getDisposableItem(viewModel.getProfile(key.getUserId()), user -> {
                                StaticData.init(key, user);
                            }, throwable -> {

                            });
                            viewModel.mainShowEvent.call();
                        } else
                            replaceFragment(new RegisterFragment(), viewModel.registerFragmentTag);
                    },
                    throwable -> {
                        replaceFragment(new AuthFragment(), viewModel.authFragmentTag);
                    });
                            /*new DisposableSingleObserver<Key>() {
                        @Override
                        public void onSuccess(Key key) {
                            accessInterceptor.setAccessKey(key.getAccessKey());
                            if (key.getUserId() != 0)
                                viewModel.mainShowEvent.call();
                            else
                                replaceFragment(new RegisterFragment(), viewModel.registerFragmentTag);
                        }

                        @Override
                        public void onError(Throwable e) {
                            replaceFragment(new AuthFragment(), viewModel.authFragmentTag);
                        }
                    }*/
        }
        scheduleJob();

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

    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, InternetService.class))
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, InternetService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, InternetService.class);
        startService(startServiceIntent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                replaceFragment(new HomeFragment(), viewModel.homeFragmentTag);
                return true;
            case R.id.navigation_feed:
                replaceFragment(new FeedFragment(), viewModel.feedFragmentTag);
                return true;
            case R.id.navigation_account:
                replaceFragment(new AccountFragment(), viewModel.accountFragmentTag);
                return true;
        }
        return false;
    }


}
