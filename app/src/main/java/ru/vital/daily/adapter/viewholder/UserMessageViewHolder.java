package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.databinding.ItemUserMessageBinding;
import ru.vital.daily.view.model.item.UserMessageItemViewModel;

public class UserMessageViewHolder extends BaseViewHolder<UserMessageItemViewModel, String> {

    public UserMessageViewHolder(ItemUserMessageBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public UserMessageItemViewModel onCreateViewModel() {
        return new UserMessageItemViewModel();
    }
}
