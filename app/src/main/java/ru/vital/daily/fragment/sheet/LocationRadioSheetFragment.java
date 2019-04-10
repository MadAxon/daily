package ru.vital.daily.fragment.sheet;

import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentLocationRadioSheetBinding;
import ru.vital.daily.view.model.LocationRadioSheetViewModel;

public class LocationRadioSheetFragment extends BaseSheetFragment<LocationRadioSheetViewModel, FragmentLocationRadioSheetBinding> {
    @Override
    protected LocationRadioSheetViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(LocationRadioSheetViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_location_radio_sheet;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }
}
