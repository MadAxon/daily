package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentConfirmationBinding;
import ru.vital.daily.view.model.ConfirmationViewModel;

public class ConfirmationFragment extends BaseFragment<ConfirmationViewModel, FragmentConfirmationBinding> {

    @Override
    protected ConfirmationViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(ConfirmationViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_confirmation;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar(dataBinding.toolbar, true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_check, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
