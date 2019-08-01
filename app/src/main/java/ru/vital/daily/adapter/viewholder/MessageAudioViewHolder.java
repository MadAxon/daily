package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;

import ru.vital.daily.view.model.item.MessageMediaItemViewModel;

public class MessageAudioViewHolder extends MessageViewHolder<MessageMediaItemViewModel> {

    public MessageAudioViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public MessageMediaItemViewModel onCreateViewModel() {
        return new MessageMediaItemViewModel();
    }
}
