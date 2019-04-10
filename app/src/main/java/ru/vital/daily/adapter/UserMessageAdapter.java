package ru.vital.daily.adapter;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.UserMessageViewHolder;
import ru.vital.daily.databinding.ItemUserMessageBinding;
import ru.vital.daily.listener.SingleLiveEvent;

public class UserMessageAdapter extends BaseAdapter<String, UserMessageViewHolder, ItemUserMessageBinding> {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_user_message;
    }

    @Override
    public UserMessageViewHolder onCreateViewHolderBinding(ItemUserMessageBinding viewDataBinding, int viewType) {
        return new UserMessageViewHolder(viewDataBinding);
    }

}
