package ru.vital.daily.fragment;

import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentBusinessBinding;
import ru.vital.daily.view.model.BusinessViewModel;

public class BusinessFragment extends BaseFragment<BusinessViewModel, FragmentBusinessBinding> {

    @Override
    protected BusinessViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(BusinessViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }
}
