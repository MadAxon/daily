package ru.vital.daily.fragment;

import android.os.Bundle;
import android.os.Environment;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.FileAdapter;
import ru.vital.daily.databinding.FragmentFileBinding;
import ru.vital.daily.view.model.FileViewModel;

public class FileFragment extends BaseFragment<FileViewModel, FragmentFileBinding> {

    @Override
    protected FileViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(FileViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_file;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).li;
        dataBinding.setAdapter(new FileAdapter(false));
    }
}
