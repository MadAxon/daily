package ru.vital.daily.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import ru.vital.daily.R;
import ru.vital.daily.view.model.ViewModel;


public abstract class BaseActivity<VM extends ViewModel, B extends ViewDataBinding>
        extends AppCompatActivity {

    private InputMethodManager inputMethodManager;

    protected VM viewModel;

    protected B dataBinding;

    protected static final int STATUS_REQUEST = 0;

    protected static final String STATUS_MESSAGE = "STATUS_MESSAGE";

    protected abstract VM onCreateViewModel();

    protected abstract @LayoutRes
    int getLayoutId();

    protected abstract @IdRes
    int getVariable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        dataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        dataBinding.setLifecycleOwner(this);
        viewModel = onCreateViewModel();
        viewModel.onCreate();
        dataBinding.setVariable(getVariable(), viewModel);
        dataBinding.executePendingBindings();

        //viewModel.getStatusLiveEvent().observe(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STATUS_REQUEST && resultCode == Activity.RESULT_OK);
            //showSuccessSnackbar(data.getStringExtra(STATUS_MESSAGE));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    protected void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    protected void setupToolbar(Toolbar toolbar, String title) {
        setTitle(title);
        setupToolbar(toolbar);
    }

}
