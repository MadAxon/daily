package ru.vital.daily.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.AudioAdapter;
import ru.vital.daily.databinding.FragmentAudioBinding;
import ru.vital.daily.view.model.AudioViewModel;

public class AudioFragment extends BaseFragment<AudioViewModel, FragmentAudioBinding> {

    @Override
    protected AudioViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(AudioViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_audio;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.setAdapter(new AudioAdapter());

    }
}
