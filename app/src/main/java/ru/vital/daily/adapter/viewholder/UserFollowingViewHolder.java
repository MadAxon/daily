package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.UserFollowingItemViewModel;

public class UserFollowingViewHolder extends BaseViewHolder<UserFollowingItemViewModel, Object> {

    public UserFollowingViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public UserFollowingItemViewModel onCreateViewModel() {
        return new UserFollowingItemViewModel();
    }
}
