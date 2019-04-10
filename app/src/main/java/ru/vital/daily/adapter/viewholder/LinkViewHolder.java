package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.LinkItemViewModel;

public class LinkViewHolder extends BaseViewHolder<LinkItemViewModel, Object> {

    public LinkViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public LinkItemViewModel onCreateViewModel() {
        return new LinkItemViewModel();
    }
}
