package ru.vital.daily.fragment;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentAuthBinding;
import ru.vital.daily.di.FragmentInjectable;
import ru.vital.daily.view.model.AuthViewModel;

public class AuthFragment extends BaseFragment<AuthViewModel, FragmentAuthBinding> {

    @Override
    protected AuthViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(AuthViewModel.class);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, false);

        viewModel.openSignEmailEvent.observe(this, aVoid -> {
            openFragment(SignInFragment.newInstance(false), viewModel.signInFragmentTag);
        });
        viewModel.openSignPhoneEvent.observe(this, aVoid -> {
            openFragment(SignInFragment.newInstance(true), viewModel.signInFragmentTag);
            //openFragment(new RegisterFragment(), viewModel.registerFragmentTag);
        });
        viewModel.openRegisterEvent.observe(this, aVoid -> {
            openFragment(new RegisterFragment(), viewModel.registerFragmentTag);
        });
    }
}
