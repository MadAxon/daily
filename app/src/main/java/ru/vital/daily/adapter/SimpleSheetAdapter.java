package ru.vital.daily.adapter;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.SimpleSheetViewHolder;
import ru.vital.daily.databinding.ItemSimpleSheetBinding;
import ru.vital.daily.repository.model.FragmentSheetModel;

public class SimpleSheetAdapter extends BaseAdapter<FragmentSheetModel, SimpleSheetViewHolder, ItemSimpleSheetBinding> {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_simple_sheet;
    }

    @Override
    public SimpleSheetViewHolder onCreateViewHolderBinding(ItemSimpleSheetBinding viewDataBinding, int viewType) {
        return new SimpleSheetViewHolder(viewDataBinding);
    }
}
