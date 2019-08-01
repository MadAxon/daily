package ru.vital.daily.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.ContactAdapter;
import ru.vital.daily.databinding.FragmentContactListBinding;
import ru.vital.daily.fragment.sheet.ChatSheetFragment;
import ru.vital.daily.view.model.ContactViewModel;

public class ContactFragmentList extends BaseFragment<ContactViewModel, FragmentContactListBinding> {

    @Override
    protected ContactViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ContactViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contact_list;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataBinding.setAdapter(new ContactAdapter());
        viewModel.users.observe(this, users -> {
            dataBinding.getAdapter().clearAdapter();
            dataBinding.getAdapter().updateItems(users);
        });
        viewModel.clearAdapterEvent.observe(this, aVoid -> dataBinding.getAdapter().clearAdapter());
        dataBinding.getAdapter().clickEvent.observe(this, user -> {
            dataBinding.getAdapter().onItemSelected(user);
            if (dataBinding.getAdapter().hasNoSelections())
                dataBinding.floatingActionButton.hide();
            else if (!dataBinding.floatingActionButton.isShown()) dataBinding.floatingActionButton.show();
        });
        viewModel.sendClickEvent.observe(this, aVoid -> {
            if (getParentFragment() != null && getParentFragment() instanceof ChatSheetFragment)
                ((ChatSheetFragment) getParentFragment()).sendSelectedContacts(dataBinding.getAdapter().getSelectedItems());
        });
    }
}
