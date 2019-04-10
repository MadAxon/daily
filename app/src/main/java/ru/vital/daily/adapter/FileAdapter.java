package ru.vital.daily.adapter;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.FileViewHolder;
import ru.vital.daily.databinding.ItemFileGridBinding;

public class FileAdapter extends BaseAdapter<Object, FileViewHolder, ItemFileGridBinding> {

    private final int LIST_VIEW_TYPE = 0,
            GRID_VIEW_TYPE = 1,
            HEADER_VIEW_TYPE = 2;

    private final boolean isList;

    public FileAdapter(boolean isList) {
        this.isList = isList;
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE:

            case LIST_VIEW_TYPE:
                return R.layout.item_file_list;
            case GRID_VIEW_TYPE:
            default:
                return R.layout.item_file_grid;
        }
    }

    @Override
    public FileViewHolder onCreateViewHolderBinding(ItemFileGridBinding viewDataBinding, int viewType) {
        return new FileViewHolder(viewDataBinding);
    }

    @Override
    public int getItemViewType(int position) {
        if (isList) return LIST_VIEW_TYPE;
        else return GRID_VIEW_TYPE;
    }
}
