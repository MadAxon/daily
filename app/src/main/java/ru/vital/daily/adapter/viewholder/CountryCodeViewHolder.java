package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.repository.model.CountryCodeModel;
import ru.vital.daily.view.model.item.CountryCodeItemViewModel;

public class CountryCodeViewHolder extends BaseViewHolder<CountryCodeItemViewModel, CountryCodeModel> {

    public CountryCodeViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public CountryCodeItemViewModel onCreateViewModel() {
        return new CountryCodeItemViewModel();
    }
}
