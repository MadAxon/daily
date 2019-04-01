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

public abstract class BaseAdapter<M, VH extends BaseViewHolder, VDB extends ViewDataBinding>
        extends RecyclerView.Adapter<VH> {

    private final ArrayList<M> items;

    public abstract @LayoutRes
    int getLayoutId();

    public abstract VH onCreateViewHolderBinding(VDB viewDataBinding);

    public BaseAdapter() {
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VDB viewDataBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId()
                , parent, false);
        return onCreateViewHolderBinding(viewDataBinding);
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

    public void updateAdapter(List<M> items) {
        if (items != null && items.size() != 0) {
            int positionStart = this.items.size();
            this.items.addAll(items);
            notifyItemRangeInserted(positionStart, items.size());
        }
    }

    public void clearAdapter() {
        items.clear();
        notifyDataSetChanged();
    }

}
