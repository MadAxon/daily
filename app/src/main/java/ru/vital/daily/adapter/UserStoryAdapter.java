package ru.vital.daily.adapter;

import androidx.annotation.NonNull;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.UserMessageViewHolder;
import ru.vital.daily.adapter.viewholder.UserStoryViewHolder;
import ru.vital.daily.databinding.ItemUserStoryBinding;
import ru.vital.daily.listener.SingleLiveEvent;

public class UserStoryAdapter extends BaseAdapter<Object, UserStoryViewHolder, ItemUserStoryBinding> {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_user_story;
    }

    @Override
    public UserStoryViewHolder onCreateViewHolderBinding(ItemUserStoryBinding viewDataBinding, int viewType) {
        return new UserStoryViewHolder(viewDataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserStoryViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.viewModel.isMyStory = position == 0;
        holder.viewModel.notifyChange(BR.isMyStory);
    }
}
