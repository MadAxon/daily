package ru.vital.daily.fragment.sheet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.SimpleSheetAdapter;
import ru.vital.daily.databinding.FragmentSimpleSheetBinding;
import ru.vital.daily.repository.model.FragmentSheetModel;
import ru.vital.daily.view.model.SimpleSheetViewModel;

public class SimpleSheetFragment extends BaseSheetFragment<SimpleSheetViewModel, FragmentSimpleSheetBinding> {

    private static final String DRAWABLE_ID_ARRAY_EXTRA = "DRAWABLE_ID_ARRAY_EXTRA",
            STRING_ID_ARRAY_EXTRA = "STRING_ID_ARRAY_EXTRA",
            TITLE_EXTRA = "TITLE_EXTRA";

    /**
     * drawableIds's length and stringIds's length must be equal!
     */
    public static SimpleSheetFragment newInstance(String title, @Nullable int[] drawableIds, @NonNull int[] stringIds) {
        Bundle args = new Bundle();
        args.putIntArray(DRAWABLE_ID_ARRAY_EXTRA, drawableIds);
        args.putIntArray(STRING_ID_ARRAY_EXTRA, stringIds);
        args.putString(TITLE_EXTRA, title);
        SimpleSheetFragment fragment = new SimpleSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * drawableIds's length and stringIds's length must be equal!
     */
    public static SimpleSheetFragment newInstance(@Nullable int[] drawableIds, @NonNull int[] stringIds) {
        Bundle args = new Bundle();
        args.putIntArray(DRAWABLE_ID_ARRAY_EXTRA, drawableIds);
        args.putIntArray(STRING_ID_ARRAY_EXTRA, stringIds);
        SimpleSheetFragment fragment = new SimpleSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SimpleSheetFragment newInstance(String title, @NonNull int[] stringIds) {
        Bundle args = new Bundle();
        args.putIntArray(STRING_ID_ARRAY_EXTRA, stringIds);
        args.putString(TITLE_EXTRA, title);
        SimpleSheetFragment fragment = new SimpleSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SimpleSheetFragment newInstance(@NonNull int[] stringIds) {
        Bundle args = new Bundle();
        args.putIntArray(STRING_ID_ARRAY_EXTRA, stringIds);
        SimpleSheetFragment fragment = new SimpleSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SimpleSheetViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(SimpleSheetViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_simple_sheet;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataBinding.setAdapter(new SimpleSheetAdapter());

        dataBinding.getAdapter().clickEvent.observe(this, fragmentSheetModel -> {
            if (getTargetFragment() != null) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_TEXT, fragmentSheetModel.getStringId());
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            } else if (getActivity() != null && getActivity() instanceof BaseSheetFragment.OnDismissListener)
                ((BaseSheetFragment.OnDismissListener) getActivity()).onDismiss(getTag(), fragmentSheetModel.getStringId());
            dismiss();
        });

        if (getArguments() != null) {
            viewModel.title = getArguments().getString(TITLE_EXTRA);

            List<FragmentSheetModel> items = new ArrayList<>();
            int[] stringIds, drawableIds;
            if ((stringIds = getArguments().getIntArray(STRING_ID_ARRAY_EXTRA)) != null)
                if ((drawableIds = getArguments().getIntArray(DRAWABLE_ID_ARRAY_EXTRA)) != null) {
                    for (int i = 0; i < stringIds.length; i++)
                        items.add(new FragmentSheetModel(drawableIds[i], stringIds[i]));
                } else {
                    for (int stringId : stringIds) items.add(new FragmentSheetModel(0, stringId));
                }
            dataBinding.getAdapter().updateItems(items);
        }
    }

}
