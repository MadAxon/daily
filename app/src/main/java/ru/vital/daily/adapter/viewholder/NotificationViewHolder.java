package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.NotificationItemViewModel;

public class NotificationViewHolder extends BaseViewHolder<NotificationItemViewModel, Object> {

    public NotificationViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public NotificationItemViewModel onCreateViewModel() {
        return new NotificationItemViewModel();
    }
}
