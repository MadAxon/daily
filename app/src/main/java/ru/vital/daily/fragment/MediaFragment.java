package ru.vital.daily.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.MediaAdapter;
import ru.vital.daily.databinding.FragmentMediaBinding;
import ru.vital.daily.view.model.MediaViewModel;

public class MediaFragment extends BaseFragment<MediaViewModel, FragmentMediaBinding> {

    @Override
    protected MediaViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(MediaViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.setAdapter(new MediaAdapter(false));
    }
}
