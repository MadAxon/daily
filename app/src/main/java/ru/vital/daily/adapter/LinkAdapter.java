package ru.vital.daily.adapter;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.adapter.viewholder.DateHeaderLargeViewHolder;
import ru.vital.daily.adapter.viewholder.LinkViewHolder;

public class LinkAdapter extends BaseAdapter<Object, BaseViewHolder, ViewDataBinding> {

    private final int SIMPLE_VIEW_TYPE = 0,
            HEADER_VIEW_TYPE = 1;

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case SIMPLE_VIEW_TYPE:
                return R.layout.item_link;
            case HEADER_VIEW_TYPE:
            default:
                return R.layout.item_date_header_large;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        switch (viewType) {
            case SIMPLE_VIEW_TYPE:
                return new LinkViewHolder(viewDataBinding);
            case HEADER_VIEW_TYPE:
            default:
                return new DateHeaderLargeViewHolder(viewDataBinding);
        }
    }
}
