package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.ChatItemViewModel;

public class ChatViewHolder extends BaseViewHolder<ChatItemViewModel, Object> {

    public ChatViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public ChatItemViewModel onCreateViewModel() {
        return new ChatItemViewModel();
    }
}
