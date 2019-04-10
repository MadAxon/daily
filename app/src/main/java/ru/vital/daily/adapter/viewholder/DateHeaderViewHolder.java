package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.DateHeaderItemViewModel;

public class DateHeaderViewHolder extends BaseViewHolder<DateHeaderItemViewModel, Object> {

    public DateHeaderViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public DateHeaderItemViewModel onCreateViewModel() {
        return new DateHeaderItemViewModel();
    }
}
