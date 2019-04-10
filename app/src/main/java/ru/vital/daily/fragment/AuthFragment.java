package ru.vital.daily.fragment;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentAuthBinding;
import ru.vital.daily.view.model.AuthViewModel;

public class AuthFragment extends BaseFragment<AuthViewModel, FragmentAuthBinding> {

    @Override
    protected AuthViewModel onCreateViewModel() {
        return ViewModelProviders.of(getActivity()).get(AuthViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_auth;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.openSignEmailEvent.observe(this, aVoid -> {
            openFragment(SignInFragment.newInstance(false));
        });
        viewModel.openSignPhoneEvent.observe(this, aVoid -> {
            openFragment(SignInFragment.newInstance(true));
        });
        viewModel.openRegisterEvent.observe(this, aVoid -> {
            openFragment(new RegisterFragment());
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, false);
    }
}
