package ru.vital.daily.adapter.viewholder;

import androidx.databinding.ViewDataBinding;
import ru.vital.daily.BR;
import ru.vital.daily.view.model.item.FileItemViewModel;

public class FileViewHolder extends BaseViewHolder<FileItemViewModel, Object> {

    public FileViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public FileItemViewModel onCreateViewModel() {
        return new FileItemViewModel();
    }
}
