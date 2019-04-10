package ru.vital.daily.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.R;
import ru.vital.daily.BR;
import ru.vital.daily.activity.CountryCodeActivity;
import ru.vital.daily.activity.MainActivity;
import ru.vital.daily.databinding.FragmentRegisterBinding;
import ru.vital.daily.view.model.RegisterViewModel;

public class RegisterFragment extends BaseFragment<RegisterViewModel, FragmentRegisterBinding> {

    private final int REQUEST_COUNTRY_CODE = 100;

    @Override
    protected RegisterViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(RegisterViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.goBackEvent.observe(this, aVoid -> onBackPressed());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        viewModel.countryClickedEvent.observe(this, aVoid -> {
            startActivityForResult(new Intent(getContext(), CountryCodeActivity.class), REQUEST_COUNTRY_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null)
            switch (requestCode) {
                case REQUEST_COUNTRY_CODE:
                    Log.i("my_logs", data.getStringExtra(CountryCodeActivity.COUNTRY_CODE));
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
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
                startActivity(new Intent(getContext(), MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
