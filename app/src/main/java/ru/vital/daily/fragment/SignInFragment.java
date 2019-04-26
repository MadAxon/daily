package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.MainActivity;
import ru.vital.daily.databinding.FragmentSignInBinding;
import ru.vital.daily.di.FragmentInjectable;
import ru.vital.daily.view.model.SignInViewModel;

public class SignInFragment extends BaseFragment<SignInViewModel, FragmentSignInBinding> implements FragmentInjectable {

    private static final String IS_PHONE_EXTRA = "IS_PHONE_EXTRA";

    public static SignInFragment newInstance(boolean isPhoneSignIn) {

        Bundle args = new Bundle();
        args.putBoolean(IS_PHONE_EXTRA, isPhoneSignIn);
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SignInViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(SignInViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign_in;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);
        if (savedInstanceState == null && getArguments() != null)
            viewModel.isPhoneSignIn = getArguments().getBoolean(IS_PHONE_EXTRA);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_check, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        hideSoftKeyboard();
        switch (item.getItemId()) {
            case R.id.menu_check:
                if (!viewModel.isLoading.get())
                viewModel.requestCode(codeHashModel -> openFragment(ConfirmationFragment.newInstance(viewModel.isPhoneSignIn, codeHashModel.getCodeHash(), viewModel.isPhoneSignIn ? viewModel.phoneRequest.getPhone() : viewModel.emailRequest.getEmail()), viewModel.confirmationFragmentTag));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
