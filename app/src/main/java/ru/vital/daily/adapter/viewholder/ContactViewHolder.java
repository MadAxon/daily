package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.ContactItemViewModel;

public class ContactViewHolder extends BaseViewHolder<ContactItemViewModel, Object> {

    public ContactViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public ContactItemViewModel onCreateViewModel() {
        return new ContactItemViewModel();
    }
}
