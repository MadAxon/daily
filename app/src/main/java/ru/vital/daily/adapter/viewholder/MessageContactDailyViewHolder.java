package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;

import ru.vital.daily.view.model.item.MessageContactDailyItemViewModel;

public class MessageContactDailyViewHolder extends MessageViewHolder<MessageContactDailyItemViewModel> {

    public MessageContactDailyViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public MessageContactDailyItemViewModel onCreateViewModel() {
        return new MessageContactDailyItemViewModel();
    }
}
