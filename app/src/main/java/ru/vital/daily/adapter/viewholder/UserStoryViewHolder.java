package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.UserStoryItemViewModel;

public class UserStoryViewHolder extends BaseViewHolder<UserStoryItemViewModel, Object> {

    public UserStoryViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public UserStoryItemViewModel onCreateViewModel() {
        return new UserStoryItemViewModel();
    }
}
