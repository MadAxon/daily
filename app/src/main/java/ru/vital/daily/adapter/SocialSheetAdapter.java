package ru.vital.daily.adapter;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.SocialSheetViewHolder;
import ru.vital.daily.databinding.ItemSocialSheetBinding;

public class SocialSheetAdapter extends BaseAdapter<Object, SocialSheetViewHolder, ItemSocialSheetBinding> {
    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_social_sheet;
    }

    @Override
    public SocialSheetViewHolder onCreateViewHolderBinding(ItemSocialSheetBinding viewDataBinding, int viewType) {
        return new SocialSheetViewHolder(viewDataBinding);
    }
}
