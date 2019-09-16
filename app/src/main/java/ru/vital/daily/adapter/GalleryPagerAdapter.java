package ru.vital.daily.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import javax.inject.Inject;

import ru.vital.daily.R;
import ru.vital.daily.databinding.ItemGalleryBinding;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.util.MediaProgressHelper;
import ru.vital.daily.util.SelectedMedias;

public class GalleryPagerAdapter extends PagerAdapter {

    public final SingleLiveEvent<ConstraintLayout> pageSelectedEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Void> clickEvent = new SingleLiveEvent<>();

    private final List<Media> items;

    public GalleryPagerAdapter(List<Media> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ItemGalleryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), R.layout.item_gallery, container, true);
        Media media = items.get(position);
        binding.setClickListener(v -> clickEvent.call());
        if (FileType.video.name().equals(media.getType())) {
            binding.setVideo(media);
        } else binding.setImage(media);
        Log.i("my_logs", "instantiateItem " + position);
        return binding.getRoot();
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        ConstraintLayout constraintLayout = (ConstraintLayout) object;
        if (!constraintLayout.equals(pageSelectedEvent.getValue()))
            pageSelectedEvent.setValue((ConstraintLayout) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        Log.i("my_logs", "destroyItem " + position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    public Media getMedia(int position) {
        return items.get(position);
    }

}
