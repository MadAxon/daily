package ru.vital.daily.fragment.sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentChatSheetBinding;
import ru.vital.daily.fragment.ContactFragment;
import ru.vital.daily.fragment.FileFragment;
import ru.vital.daily.fragment.LocationFragment;
import ru.vital.daily.fragment.MediaFragment;
import ru.vital.daily.fragment.MoneyFragment;
import ru.vital.daily.view.model.ChatSheetViewModel;

public class ChatSheetFragment extends BaseSheetFragment<ChatSheetViewModel, FragmentChatSheetBinding> {

    @Override
    protected ChatSheetViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(ChatSheetViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat_sheet;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        dataBinding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(dataBinding.tabLayout));
        dataBinding.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(dataBinding.viewPager));
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new MediaFragment();
                case 2:
                    return new ContactFragment();
                case 3:
                    return new FileFragment();
                case 4:
                    return new MoneyFragment();
                case 5:
                default:
                    return new LocationFragment();
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    }

}
