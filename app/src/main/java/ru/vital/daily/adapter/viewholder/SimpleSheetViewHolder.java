package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.repository.model.FragmentSheetModel;
import ru.vital.daily.view.model.item.SimpleSheetItemViewModel;

public class SimpleSheetViewHolder extends BaseViewHolder<SimpleSheetItemViewModel, FragmentSheetModel> {

    public SimpleSheetViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public SimpleSheetItemViewModel onCreateViewModel() {
        return new SimpleSheetItemViewModel();
    }
}
