package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.DateHeaderLargeItemViewModel;

public class DateHeaderLargeViewHolder extends BaseViewHolder<DateHeaderLargeItemViewModel, Object> {

    public DateHeaderLargeViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public DateHeaderLargeItemViewModel onCreateViewModel() {
        return new DateHeaderLargeItemViewModel();
    }
}
