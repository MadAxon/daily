package ru.vital.daily.adapter;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.ContactViewHolder;
import ru.vital.daily.databinding.ItemContactBinding;

public class ContactAdapter extends BaseAdapter<Object, ContactViewHolder, ItemContactBinding> {
    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_contact;
    }

    @Override
    public ContactViewHolder onCreateViewHolderBinding(ItemContactBinding viewDataBinding, int viewType) {
        return new ContactViewHolder(viewDataBinding);
    }
}
