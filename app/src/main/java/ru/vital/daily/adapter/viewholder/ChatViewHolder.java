package ru.vital.daily.adapter.viewholder;

import ru.vital.daily.BR;
import ru.vital.daily.databinding.ItemChatBinding;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.view.model.item.ChatItemViewModel;

public class ChatViewHolder extends BaseViewHolder<ChatItemViewModel, Chat> {

    public ChatViewHolder(ItemChatBinding viewDataBinding) {
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
