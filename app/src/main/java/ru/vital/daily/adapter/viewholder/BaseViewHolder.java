package ru.vital.daily.adapter.viewholder;

import androidx.annotation.IdRes;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.view.model.item.ItemViewModel;

public abstract class BaseViewHolder<VM extends ItemViewModel<M>, M> extends RecyclerView.ViewHolder {

    private final ViewDataBinding viewDataBinding;

    public final VM viewModel;

    public abstract @IdRes
    int getVariable();

    public abstract VM onCreateViewModel();

    public BaseViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        this.viewDataBinding = viewDataBinding;
        viewModel = onCreateViewModel();
    }

    public void bind(M model) {
        viewModel.setItem(model);
        viewDataBinding.setVariable(getVariable(), viewModel);
        viewDataBinding.executePendingBindings();
    }

    public void unbind() {
        if (viewDataBinding != null) viewDataBinding.unbind();
    }

}
