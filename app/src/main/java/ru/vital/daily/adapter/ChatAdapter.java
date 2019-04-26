package ru.vital.daily.adapter;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.ChatViewHolder;
import ru.vital.daily.databinding.ItemChatBinding;

public class ChatAdapter extends BaseAdapter<String, ChatViewHolder, ItemChatBinding> {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_chat;
    }

    @Override
    public ChatViewHolder onCreateViewHolderBinding(ItemChatBinding viewDataBinding, int viewType) {
        return new ChatViewHolder(viewDataBinding);
    }

}
