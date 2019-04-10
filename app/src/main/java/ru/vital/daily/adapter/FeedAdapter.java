package ru.vital.daily.adapter;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.FeedViewHolder;
import ru.vital.daily.enums.ItemViewType;
import ru.vital.daily.listener.SingleLiveEvent;

public class FeedAdapter extends BaseAdapter<Object, FeedViewHolder, ViewDataBinding> {

    private final int ITEM_VIEW_TYPE_LIST = 0,
                    ITEM_VIEW_TYPE_GRID = 1;

    private ItemViewType itemViewType = ItemViewType.list;

    public SingleLiveEvent<Void> userClickedEvent = new SingleLiveEvent<>();

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case ITEM_VIEW_TYPE_GRID:
                return R.layout.item_feed_grid;
            case ITEM_VIEW_TYPE_LIST:
                return R.layout.item_feed_list;
            default:
                return R.layout.item_feed_list;
        }
    }

    @Override
    public FeedViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        FeedViewHolder viewHolder = new FeedViewHolder(viewDataBinding);
        viewHolder.viewModel.userClickedEvent = userClickedEvent;
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        switch (itemViewType) {
            case grid:
                return ITEM_VIEW_TYPE_GRID;
            case list:
                return ITEM_VIEW_TYPE_LIST;
            default:
                return ITEM_VIEW_TYPE_LIST;
        }
    }

    public boolean changeCurrentItemViewType(ItemViewType itemViewType) {
       if (this.itemViewType != itemViewType) {
           this.itemViewType = itemViewType;
           return true;
       }
       return false;
    }
}
