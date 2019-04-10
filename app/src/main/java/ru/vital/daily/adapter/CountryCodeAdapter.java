package ru.vital.daily.adapter;

import java.util.Set;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.CountryCodeViewHolder;
import ru.vital.daily.databinding.ItemCountryCodeBinding;
import ru.vital.daily.repository.model.CountryCodeModel;

public class CountryCodeAdapter extends BaseAdapter<CountryCodeModel, CountryCodeViewHolder, ItemCountryCodeBinding> {

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.item_country_code;
    }

    @Override
    public CountryCodeViewHolder onCreateViewHolderBinding(ItemCountryCodeBinding viewDataBinding, int viewType) {
        return new CountryCodeViewHolder(viewDataBinding);
    }
}
