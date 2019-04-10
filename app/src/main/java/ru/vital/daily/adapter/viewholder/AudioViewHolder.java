package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.AudioItemViewModel;

public class AudioViewHolder extends BaseViewHolder<AudioItemViewModel, Object> {

    public AudioViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public AudioItemViewModel onCreateViewModel() {
        return new AudioItemViewModel();
    }
}
