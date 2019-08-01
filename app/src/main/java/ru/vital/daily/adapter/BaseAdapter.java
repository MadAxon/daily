package ru.vital.daily.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.listener.SingleLiveEvent;

public abstract class BaseAdapter<M, VH extends BaseViewHolder, VDB extends ViewDataBinding>
        extends RecyclerView.Adapter<VH> {

    protected final ArrayList<M> items;

    public final SingleLiveEvent<M> clickEvent;

    public abstract @LayoutRes
    int getLayoutId(int viewType);

    public abstract VH onCreateViewHolderBinding(VDB viewDataBinding, int viewType);

    public BaseAdapter() {
        items = new ArrayList<>();
        clickEvent = new SingleLiveEvent<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VDB viewDataBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(viewType)
                , parent, false);
        VH viewHolder = onCreateViewHolderBinding(viewDataBinding, viewType);
        viewHolder.viewModel.setClickEvent(clickEvent);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        M item = items.get(position);
        holder.bind(item);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VH holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<M> items) {
        if (items != null && items.size() != 0) {
            int positionStart = this.items.size();
            this.items.clear();
            this.items.addAll(items);
            notifyItemRangeInserted(positionStart, items.size());
        }
    }

    public void clearAdapter() {
        items.clear();
        notifyDataSetChanged();
    }

    public ArrayList<M> getItems() {
        return items;
    }
}
