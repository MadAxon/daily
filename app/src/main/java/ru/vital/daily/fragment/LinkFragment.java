package ru.vital.daily.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.LinkAdapter;
import ru.vital.daily.adapter.viewholder.LinkViewHolder;
import ru.vital.daily.databinding.FragmentLinkBinding;
import ru.vital.daily.view.model.LinkViewModel;

public class LinkFragment extends BaseFragment<LinkViewModel, FragmentLinkBinding> {
    @Override
    protected LinkViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(LinkViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_link;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.setAdapter(new LinkAdapter());
    }
}
