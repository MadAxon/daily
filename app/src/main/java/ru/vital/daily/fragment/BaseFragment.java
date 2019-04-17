package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import androidx.lifecycle.ViewModelProvider;
import dagger.android.support.AndroidSupportInjection;
import ru.vital.daily.R;
import ru.vital.daily.view.model.ViewModel;

public abstract class BaseFragment<VM extends ViewModel, B extends ViewDataBinding>
        extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    VM viewModel;

    protected B dataBinding;

    protected abstract VM onCreateViewModel();

    protected abstract @LayoutRes
    int getLayoutId();

    protected abstract @IdRes
    int getVariable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = onCreateViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        dataBinding.setVariable(getVariable(), viewModel);
        return dataBinding.getRoot();
    }

    protected void openFragment(Fragment fragment) {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
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
}
