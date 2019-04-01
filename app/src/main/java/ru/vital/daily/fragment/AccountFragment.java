package ru.vital.daily.fragment;

import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentAccountBinding;
import ru.vital.daily.view.model.MainViewModel;

public class AccountFragment extends BaseFragment<MainViewModel, FragmentAccountBinding> {

    @Override
    protected MainViewModel onCreateViewModel() {
        return ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }
}
