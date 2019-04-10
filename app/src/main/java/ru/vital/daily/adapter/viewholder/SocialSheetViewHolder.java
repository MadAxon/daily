package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.SocialSheetItemViewModel;

public class SocialSheetViewHolder extends BaseViewHolder<SocialSheetItemViewModel, Object> {

    public SocialSheetViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public SocialSheetItemViewModel onCreateViewModel() {
        return new SocialSheetItemViewModel();
    }
}
