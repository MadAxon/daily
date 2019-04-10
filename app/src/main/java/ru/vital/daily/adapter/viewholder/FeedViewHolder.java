package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.FeedItemViewModel;

public class FeedViewHolder extends BaseViewHolder<FeedItemViewModel, Object> {

    public FeedViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public FeedItemViewModel onCreateViewModel() {
        return new FeedItemViewModel();
    }
}
