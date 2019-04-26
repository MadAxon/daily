package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import ru.vital.daily.R;
import ru.vital.daily.broadcast.ConnectivityBroadcast;
import ru.vital.daily.di.FragmentInjectable;
import ru.vital.daily.util.SnackbarProvider;
import ru.vital.daily.view.model.ViewModel;

public abstract class BaseFragment<VM extends ViewModel, B extends ViewDataBinding>
        extends Fragment implements FragmentInjectable, Observer<Throwable> {

    @Inject
    InputMethodManager inputMethodManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    ConnectivityBroadcast connectivityBroadcast;

    VM viewModel;

    protected B dataBinding;

    protected abstract VM onCreateViewModel();

    protected abstract @LayoutRes
    int getLayoutId();

    protected abstract @IdRes
    int getVariable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        //dataBinding.setVariable(getVariable(), viewModel);
        return dataBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = onCreateViewModel();
        dataBinding.setVariable(getVariable(), viewModel);
        viewModel.errorEvent.observe(this, this);
    }

    protected void openFragment(Fragment fragment, String tag) {
        if (getActivity() != null && getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
    }

    protected void replaceFragment(Fragment fragment, String tag) {
        if (getActivity() != null && getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, tag)
                    .addToBackStack(null)
                    .commit();
    }

    protected void openSheetFragment(BottomSheetDialogFragment sheetDialogFragment, String tag) {
        if (getChildFragmentManager().findFragmentByTag(tag) == null)
            sheetDialogFragment.show(getChildFragmentManager(), tag);
    }

    protected void openSheetFragment(BottomSheetDialogFragment sheetDialogFragment, String tag, int requestCode) {
        if (getActivity() != null && getActivity().getSupportFragmentManager().findFragmentByTag(tag) == null) {
            sheetDialogFragment.setTargetFragment(this, requestCode);
            sheetDialogFragment.show(getActivity().getSupportFragmentManager(), tag);
        }
    }

    protected void onBackPressed() {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    protected void setupToolbar(Toolbar toolbar, boolean includeBackButton) {
        AppCompatActivity appCompatActivity;
        if ((appCompatActivity = (AppCompatActivity) getActivity()) != null) {
            appCompatActivity.setSupportActionBar(toolbar);
            assert appCompatActivity.getSupportActionBar() != null;
            appCompatActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(includeBackButton);
            appCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(includeBackButton);
            appCompatActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
            setHasOptionsMenu(true);
        }
    }

    protected void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(dataBinding.getRoot().getWindowToken(), 0);
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
}
