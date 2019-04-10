package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.MediaItemViewModel;

public class MediaViewHolder extends BaseViewHolder<MediaItemViewModel, Object> {

    public MediaViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public MediaItemViewModel onCreateViewModel() {
        return new MediaItemViewModel();
    }
}
