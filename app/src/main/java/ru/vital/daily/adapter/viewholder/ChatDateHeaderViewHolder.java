package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.ChatDateHeaderItemViewModel;

public class ChatDateHeaderViewHolder extends BaseViewHolder<ChatDateHeaderItemViewModel, String> {

    public ChatDateHeaderViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public ChatDateHeaderItemViewModel onCreateViewModel() {
        return new ChatDateHeaderItemViewModel();
    }
}
