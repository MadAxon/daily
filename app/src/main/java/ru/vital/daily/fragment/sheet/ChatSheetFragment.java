package ru.vital.daily.fragment.sheet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import javax.inject.Inject;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentChatSheetBinding;
import ru.vital.daily.di.FragmentInjectable;
import ru.vital.daily.fragment.AlbumFragment;
import ru.vital.daily.fragment.CameraFragment;
import ru.vital.daily.fragment.ContactFragmentList;
import ru.vital.daily.fragment.FileFragment;
import ru.vital.daily.fragment.LocationFragment;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.util.MediaProgressHelper;
import ru.vital.daily.view.model.ChatSheetViewModel;
import ru.vital.daily.view.model.ChatViewModel;

public class ChatSheetFragment extends ViewPagerBottomSheetDialogFragment implements FragmentInjectable {

    private final int REQUEST_MEDIA_CODE = 101;

    private ChatSheetViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    MediaProgressHelper mediaProgressHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentChatSheetBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_sheet, container, false);
        binding.setVariable(BR.viewModel, viewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatSheetViewModel.class));
        binding.container.setAdapter(new PagerAdapter(getChildFragmentManager()));
        binding.container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(binding.container));

        BottomSheetUtils.setupViewPager(binding.container);
        return binding.getRoot();
    }

    public void sendSelectedMedias(LongSparseArray<Media> medias) {
        ChatViewModel chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        final int size = medias.size();
        for (int i = 0; i < size; i++) {
            Media media = medias.valueAt(i);
            media.setSelected(false);
            media.setProgress(0f);
            mediaProgressHelper.putMedia(media);
        }
        chatViewModel.sendMessage(medias);
        dismiss();
    }

    public void sendSelectedContacts(LongSparseArray<User> users) {
        ChatViewModel chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        chatViewModel.sendAccounts(users);
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_MEDIA_CODE:
                    viewModel.cameraSheetClickEvent.setValue(data.getIntExtra(Intent.EXTRA_TEXT, R.string.sheet_avatar_gallery));
                    break;
            }
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
                    return new CameraFragment();
                case 1:
                    return new AlbumFragment();
                case 2:
                    return new ContactFragmentList();
                case 3:
                    return new FileFragment();
                /*case 4:
                    return new MoneyFragment();*/
                case 4:
                default:
                    return new LocationFragment();
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

}
