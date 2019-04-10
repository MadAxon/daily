package ru.vital.daily.adapter;

import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.adapter.viewholder.DateHeaderViewHolder;
import ru.vital.daily.adapter.viewholder.NotificationViewHolder;
import ru.vital.daily.adapter.viewholder.UserFollowingViewHolder;

public class NotificationAdapter extends BaseAdapter<Object, BaseViewHolder, ViewDataBinding> {

    private final int HEADER_VIEW_TYPE = 0,
                    SIMPLE_VIEW_TYPE = 1,
                    FOLLOWING_VIEW_TYPE = 2;

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE:
                return R.layout.item_date_header;
            case SIMPLE_VIEW_TYPE:
            default:
                return R.layout.item_notification;
            case FOLLOWING_VIEW_TYPE:
                return R.layout.item_user_following;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE:
                return new DateHeaderViewHolder(viewDataBinding);
            case SIMPLE_VIEW_TYPE:
            default:
                return new NotificationViewHolder(viewDataBinding);
            case FOLLOWING_VIEW_TYPE:
                return new UserFollowingViewHolder(viewDataBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
