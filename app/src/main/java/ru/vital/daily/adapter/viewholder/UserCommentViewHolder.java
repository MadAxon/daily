package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.UserCommentItemViewModel;

public class UserCommentViewHolder extends BaseViewHolder<UserCommentItemViewModel, Object> {

    public UserCommentViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public UserCommentItemViewModel onCreateViewModel() {
        return new UserCommentItemViewModel();
    }
}
