package ru.vital.daily.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentMoneyBinding;
import ru.vital.daily.view.model.MoneyViewModel;

public class MoneyFragment extends BaseFragment<MoneyViewModel, FragmentMoneyBinding> {
    @Override
    protected MoneyViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(MoneyViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_money;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel.sendMoneyClickedEvent.observe(this, aVoid -> {

        });
    }
}
