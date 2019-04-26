package ru.vital.daily.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.ChatCreateActivity;
import ru.vital.daily.adapter.ChatAdapter;
import ru.vital.daily.databinding.FragmentHomeBinding;
import ru.vital.daily.view.model.HomeViewModel;

public class HomeFragment extends BaseFragment<HomeViewModel, FragmentHomeBinding> {

    @Override
    protected HomeViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, false);
        dataBinding.setAdapter(new ChatAdapter());
        dataBinding.getAdapter().clickEvent.observe(this, aVoid -> {
        });
        viewModel.toolbarClickedEvent.observe(this, aVoid -> {
            Log.i("my_logs", "clicked");
        });
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("first");
        arrayList.add("second");
        arrayList.add("third");
        dataBinding.getAdapter().updateItems(arrayList);

        dataBinding.textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    dataBinding.textInputLayout.setError("Limit is reached");
                    dataBinding.textInputEditText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_search), null, null, null);
                }
                else {
                    dataBinding.textInputLayout.setErrorEnabled(false);
                    dataBinding.textInputEditText.setCompoundDrawables(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                startActivity(new Intent(getContext(), ChatCreateActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
