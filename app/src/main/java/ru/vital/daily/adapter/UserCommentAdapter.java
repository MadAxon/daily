package ru.vital.daily.adapter;

import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.UserCommentViewHolder;
import ru.vital.daily.databinding.ItemUserCommentBinding;

public class UserCommentAdapter extends BaseAdapter<Object, UserCommentViewHolder, ItemUserCommentBinding> {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_user_comment;
    }

    @Override
    public UserCommentViewHolder onCreateViewHolderBinding(ItemUserCommentBinding viewDataBinding, int viewType) {
        return new UserCommentViewHolder(viewDataBinding);
    }
}
