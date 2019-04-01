package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import ru.vital.daily.view.model.ViewModel;

public abstract class BaseFragment<VM extends ViewModel, B extends ViewDataBinding>
        extends Fragment {

    protected VM viewModel;

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
}
