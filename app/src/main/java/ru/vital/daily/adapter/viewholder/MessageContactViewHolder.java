package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;

import ru.vital.daily.view.model.item.MessageContactItemViewModel;

public class MessageContactViewHolder extends MessageViewHolder<MessageContactItemViewModel> {

    public MessageContactViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public MessageContactItemViewModel onCreateViewModel() {
        return new MessageContactItemViewModel();
    }
}
