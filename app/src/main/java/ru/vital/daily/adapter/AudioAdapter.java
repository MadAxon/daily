package ru.vital.daily.adapter;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.AudioViewHolder;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.adapter.viewholder.DateHeaderLargeViewHolder;

public class AudioAdapter extends BaseAdapter<Object, BaseViewHolder, ViewDataBinding> {

    private final int SIMPLE_VIEW_TYPE = 0,
            HEADER_VIEW_TYPE = 1;

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE:
                return R.layout.item_date_header_large;
            case SIMPLE_VIEW_TYPE:
            default:
                return R.layout.item_audio;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        switch (viewType) {
            case SIMPLE_VIEW_TYPE:
                return new AudioViewHolder(viewDataBinding);
            case HEADER_VIEW_TYPE:
            default:
                return new DateHeaderLargeViewHolder(viewDataBinding);
        }
    }
}
