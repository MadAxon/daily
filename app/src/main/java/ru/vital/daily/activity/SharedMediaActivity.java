package ru.vital.daily.activity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ActivitySharedMediaBinding;
import ru.vital.daily.fragment.AudioFragment;
import ru.vital.daily.fragment.FileFragment;
import ru.vital.daily.fragment.LinkFragment;
import ru.vital.daily.fragment.MediaFragment;
import ru.vital.daily.view.model.SharedMediaViewModel;

public class SharedMediaActivity extends BaseActivity<SharedMediaViewModel, ActivitySharedMediaBinding> {
    @Override
    protected SharedMediaViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(SharedMediaViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shared_media;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);
        dataBinding.viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
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
                case 0:
                    return new MediaFragment();
                case 1:
                    return new MediaFragment();
                case 2:
                    return new AudioFragment();
                case 3:
                    return new FileFragment();
                case 4:
                default:
                    return new LinkFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
