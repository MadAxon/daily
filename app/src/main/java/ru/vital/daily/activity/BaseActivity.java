package ru.vital.daily.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import ru.vital.daily.R;
import ru.vital.daily.broadcast.ConnectivityBroadcast;
import ru.vital.daily.util.SnackbarProvider;
import ru.vital.daily.view.model.ViewModel;


public abstract class BaseActivity<VM extends ViewModel, B extends ViewDataBinding>
        extends AppCompatActivity implements HasSupportFragmentInjector, Observer<Throwable> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

    @Inject
    ConnectivityBroadcast connectivityBroadcast;

    @Inject
    InputMethodManager inputMethodManager;

    @Inject
    IntentFilter connectivityIntentFilter;

    protected VM viewModel;

    protected B dataBinding;

    protected abstract VM onCreateViewModel();

    protected abstract @LayoutRes
    int getLayoutId();

    protected abstract @IdRes
    int getVariable();

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentDispatchingAndroidInjector;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        /*connectivityBroadcast.isOnline.observe(this, isOnline -> {
            if (isOnline)
                SnackbarProvider.getSuccessSnackbar(dataBinding.getRoot(), getString(R.string.connectivity_alive)).show();
            else SnackbarProvider.getWarnSnackbar(dataBinding.getRoot(), getString(R.string.connectivity_lost)).show();
        });*/

        dataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        dataBinding.setLifecycleOwner(this);
        viewModel = onCreateViewModel();
        viewModel.onCreate();
        dataBinding.setVariable(getVariable(), viewModel);
        dataBinding.executePendingBindings();

        viewModel.errorEvent.observe(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
        registerReceiver(connectivityBroadcast, connectivityIntentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectivityBroadcast);
    }

    protected void setupToolbar(Toolbar toolbar, boolean includeBackButton) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(includeBackButton);
        getSupportActionBar().setDisplayShowHomeEnabled(includeBackButton);
    }

    protected void addFragment(Fragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
    }

    protected void replaceFragment(Fragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .commit();
    }

    protected void openSheetFragment(BottomSheetDialogFragment sheetDialogFragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            sheetDialogFragment.show(getSupportFragmentManager(), tag);
        }
    }

    @Override
    public void onChanged(Throwable error) {
        if (error instanceof UnknownHostException && connectivityBroadcast.isOnline.getValue() != null
                && !connectivityBroadcast.isOnline.getValue())
            SnackbarProvider.getWarnSnackbar(dataBinding.getRoot(), getString(R.string.connectivity_lost)).show();
        else if (error instanceof SocketTimeoutException)
            SnackbarProvider.getWarnSnackbar(dataBinding.getRoot(), getString(R.string.connectivity_timeout)).show();
        else SnackbarProvider.getWarnSnackbar(dataBinding.getRoot(), error.getMessage()).show();
    }

    protected void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(dataBinding.getRoot().getWindowToken(), 0);
    }
}
