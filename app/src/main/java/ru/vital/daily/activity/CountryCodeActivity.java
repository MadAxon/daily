package ru.vital.daily.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.CountryCodeAdapter;
import ru.vital.daily.databinding.ActivityCountryCodeBinding;
import ru.vital.daily.repository.model.CountryCodeModel;
import ru.vital.daily.view.model.CountryCodeViewModel;

public class CountryCodeActivity extends BaseActivity<CountryCodeViewModel, ActivityCountryCodeBinding> {

    public static final String COUNTRY_CODE = "COUNTRY_CODE",
                            COUNTRY_NAME = "COUNTRY_NAME";

    @Override
    protected CountryCodeViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(CountryCodeViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_country_code;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupToolbar(dataBinding.toolbar, true);
        dataBinding.setAdapter(new CountryCodeAdapter());

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(this);
        Set<String> countryCodes = phoneNumberUtil.getSupportedRegions();
        List<CountryCodeModel> items = new ArrayList<>();
        for (String countryCode: countryCodes)
            items.add(new CountryCodeModel(new Locale(Locale.getDefault().getCountry(), countryCode).getDisplayCountry(),
                    phoneNumberUtil.getCountryCodeForRegion(countryCode)));
        Collections.sort(items, (o1, o2) -> o1.getCountryName().compareTo(o2.getCountryName()));

        String countryName = "";
        for (CountryCodeModel countryCodeModel : items) {
            if (!countryName.equals(countryCodeModel.getCountryName().substring(0, 1)))
                countryCodeModel.setFirstLetter(countryName = countryCodeModel.getCountryName().substring(0, 1));
        }
        dataBinding.getAdapter().updateItems(items);

        dataBinding.getAdapter().clickEvent.observe(this, countryCodeModel -> {
            Intent intent = new Intent();
            intent.putExtra(COUNTRY_CODE, countryCodeModel.getPhoneCode());
            intent.putExtra(COUNTRY_NAME, countryCodeModel.getCountryName());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }
}
