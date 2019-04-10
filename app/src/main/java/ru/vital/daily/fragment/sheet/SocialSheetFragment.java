package ru.vital.daily.fragment.sheet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.SocialSheetAdapter;
import ru.vital.daily.databinding.FragmentSocialSheetBinding;
import ru.vital.daily.view.model.SocialSheetViewModel;

public class SocialSheetFragment extends BaseSheetFragment<SocialSheetViewModel, FragmentSocialSheetBinding> {

    @Override
    protected SocialSheetViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(SocialSheetViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_social_sheet;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.setAdapter(new SocialSheetAdapter());
    }
}
