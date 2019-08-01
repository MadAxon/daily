package ru.vital.daily.adapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.ContactViewHolder;
import ru.vital.daily.databinding.ItemContactBinding;
import ru.vital.daily.repository.data.User;

public class ContactAdapter extends BaseAdapter<User, ContactViewHolder, ItemContactBinding> {

    private final LongSparseArray<User> selectedItems = new LongSparseArray<>();

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_contact;
    }

    @Override
    public ContactViewHolder onCreateViewHolderBinding(ItemContactBinding viewDataBinding, int viewType) {
        return new ContactViewHolder(viewDataBinding);
    }

    /*@Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.viewModel.setSelected(selectedItems.get(items.get(position).getId(), false));
    }*/

    public void onItemSelected(User user) {
        if (selectedItems.get(user.getId(), null) != null) {
            selectedItems.remove(user.getId());
            user.setSelected(false);
        } else {
            selectedItems.put(user.getId(), user);
            user.setSelected(true);
        }
    }

    public long[] getSelectedIds() {
        long[] ids = new long[selectedItems.size()];
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            ids[i] = selectedItems.keyAt(i);
            selectedItems.removeAt(i);
        }
        return ids;
    }

    public List<User> getSelectedUsers() {
        List<User> users = new ArrayList<>();
        for (int i = selectedItems.size() - 1; i >= 0; i--) {
            users.add(selectedItems.valueAt(i));
        }
        return users;
    }

    public LongSparseArray<User> getSelectedItems() {
        return selectedItems;
    }

    public boolean hasNoSelections() {
        return selectedItems.isEmpty();
    }
}
