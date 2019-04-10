package ru.vital.daily.fragment;

import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentLocationBinding;
import ru.vital.daily.view.model.LocationViewModel;

public class LocationFragment extends BaseFragment<LocationViewModel, FragmentLocationBinding> {
    @Override
    protected LocationViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(LocationViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_location;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }
}
