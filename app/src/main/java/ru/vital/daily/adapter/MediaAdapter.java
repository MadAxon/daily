package ru.vital.daily.adapter;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.adapter.viewholder.DateHeaderLargeViewHolder;
import ru.vital.daily.adapter.viewholder.MediaViewHolder;
import ru.vital.daily.databinding.ItemMediaGridBinding;

public class MediaAdapter extends BaseAdapter<Object, BaseViewHolder, ViewDataBinding> {

    private final int SIMPLE_VIEW_TYPE = 0,
                    HEADER_VIEW_TYPE = 1;

    private final boolean shouldShowHeaders;

    public MediaAdapter(boolean shouldShowHeaders) {
        this.shouldShowHeaders = shouldShowHeaders;
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case SIMPLE_VIEW_TYPE:
                return R.layout.item_media_grid;
            case HEADER_VIEW_TYPE:
            default:
                return R.layout.item_date_header_large;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        switch (viewType) {
            case SIMPLE_VIEW_TYPE:
                return new MediaViewHolder(viewDataBinding);
            case HEADER_VIEW_TYPE:
            default:
                return new DateHeaderLargeViewHolder(viewDataBinding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (shouldShowHeaders) {
            return position;
        } else {
            return SIMPLE_VIEW_TYPE;
        }
    }
}
