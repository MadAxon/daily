package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.view.model.item.MessageItemViewModel;

public class MessageViewHolder extends BaseViewHolder<MessageItemViewModel, Message> {

    public MessageViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public MessageItemViewModel onCreateViewModel() {
        return new MessageItemViewModel();
    }
}
