package ru.vital.daily.fragment;

import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentContactBinding;
import ru.vital.daily.view.model.ContactViewModel;

public class ContactFragment extends BaseFragment<ContactViewModel, FragmentContactBinding> {
    @Override
    protected ContactViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(ContactViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }
}
