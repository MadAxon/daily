package ru.vital.daily.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentConfirmationBinding;
import ru.vital.daily.repository.api.AccessInterceptor;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.util.StaticData;
import ru.vital.daily.view.model.ConfirmationViewModel;
import ru.vital.daily.view.model.MainViewModel;

public class ConfirmationFragment extends BaseFragment<ConfirmationViewModel, FragmentConfirmationBinding> {

    @Inject
    AccessInterceptor accessInterceptor;

    @Inject
    DailySocket dailySocket;

    private static final String CODE_HASH_EXTRA = "CODE_HASH_EXTRA",
            IS_PHONE_EXTRA = "IS_PHONE_EXTRA",
            LOGIN_EXTRA = "LOGIN_EXTRA";

    public static ConfirmationFragment newInstance(boolean isPhoneSignIn, String codeHash, String login) {

        Bundle args = new Bundle();
        args.putBoolean(IS_PHONE_EXTRA, isPhoneSignIn);
        args.putString(CODE_HASH_EXTRA, codeHash);
        args.putString(LOGIN_EXTRA, login);
        ConfirmationFragment fragment = new ConfirmationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ConfirmationViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ConfirmationViewModel.class);
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

        if (getArguments() != null) {
            viewModel.isPhoneSignIn = getArguments().getBoolean(IS_PHONE_EXTRA);
            viewModel.codeHash = getArguments().getString(CODE_HASH_EXTRA);
            if (viewModel.isPhoneSignIn)
                viewModel.phoneRequest.setPhone(getArguments().getString(LOGIN_EXTRA));
            else viewModel.emailRequest.setEmail(getArguments().getString(LOGIN_EXTRA));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_check_1, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check_1:
                hideSoftKeyboard();
                viewModel.signIn(key -> {
                    dailySocket.connect(key.getAccessKey());
                    accessInterceptor.setAccessKey(key.getAccessKey());
                    if (key.getIsNewProfile())
                        openFragment(new RegisterFragment(), viewModel.registerFragmentTag);
                    else viewModel.updateKeyUserId(user -> {
                        StaticData.init(key, user);
                        openMainPage();
                    });/*if (key.getUserId() == 0)
                        viewModel.updateKeyUserId(aVoid -> openMainPage());
                    else openMainPage();*/
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMainPage() {
        MainViewModel mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        mainViewModel.mainShowEvent.call();
    }
}
