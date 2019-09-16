package ru.vital.daily.adapter;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.LongSparseArray;
import androidx.databinding.ViewDataBinding;

import javax.inject.Inject;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.BaseViewHolder;
import ru.vital.daily.adapter.viewholder.DateHeaderLargeViewHolder;
import ru.vital.daily.adapter.viewholder.MediaViewHolder;
import ru.vital.daily.databinding.ItemMediaGridBinding;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.util.SelectedMedias;

public class MediaAdapter extends BaseAdapter<Long, BaseViewHolder, ViewDataBinding> {

    public final SingleLiveEvent<Media> checkClickEvent = new SingleLiveEvent<>();

    private final int SIMPLE_VIEW_TYPE = 0,
            HEADER_VIEW_TYPE = 1,
            CAMERA_VIEW_TYPE = 2,
            SIMPLE_SMALL_VIEW_TYPE = 3,
            ALBUM_VIEW_TYPE = 4;

    private final boolean shouldShowHeaders;

    private int currentViewType;

    private LongSparseArray<Media> medias;

    public MediaAdapter(boolean shouldShowHeaders, int currentViewType) {
        this.shouldShowHeaders = shouldShowHeaders;
        this.currentViewType = currentViewType;
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case SIMPLE_VIEW_TYPE:
                return R.layout.item_media_grid;
            case CAMERA_VIEW_TYPE:
                return R.layout.item_camera;
            case SIMPLE_SMALL_VIEW_TYPE:
                return R.layout.item_media_grid_small;
            case ALBUM_VIEW_TYPE:
                return R.layout.item_media_album_grid;
            case HEADER_VIEW_TYPE:
            default:
                return R.layout.item_date_header_large;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolderBinding(ViewDataBinding viewDataBinding, int viewType) {
        switch (viewType) {
            case CAMERA_VIEW_TYPE:
            case SIMPLE_VIEW_TYPE:
            case SIMPLE_SMALL_VIEW_TYPE:
            case ALBUM_VIEW_TYPE:
                MediaViewHolder mediaViewHolder = new MediaViewHolder(viewDataBinding);
                mediaViewHolder.viewModel.setCheckClickEvent(checkClickEvent);
                mediaViewHolder.viewModel.setMediaClickEvent(clickEvent);
                return mediaViewHolder;
            case HEADER_VIEW_TYPE:
            default:
                return new DateHeaderLargeViewHolder(viewDataBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Media media = medias.get(items.get(position));
        holder.bind(media);
    }

    @Override
    public int getItemViewType(int position) {
        if (shouldShowHeaders) {
            return position;
        } else {
            if (position == 0 && items.get(position) == 0L)
                return CAMERA_VIEW_TYPE;
            return currentViewType;
        }
    }

    public void addCapturedMedia(Media media) {
        items.add(1, media.getId());
        medias.put(media.getId(), media);
        notifyItemInserted(1);
    }

    public void addMedia(Media media) {
        items.add(media.getId());
        medias.put(media.getId(), media);
    }

    public void notifyInserted() {
        notifyItemRangeInserted(0, items.size());
    }

    public void updateMedias(List<Long> medias) {
        //items.add(0L);
        items.clear();
        //selectedMedias.getMedias().clear();
        items.addAll(medias);
        //this.items.clear();
        //this.items.addAll(medias);
        notifyItemRangeInserted(0, items.size());
    }

    /*public void updateMedias(List<Media> medias) {
        //items.add(0L);
        items.clear();
        //selectedMedias.getMedias().clear();
        for (Media media: medias) {
            items.add(media.getId());
            //selectedMedias.getMedias().put(media.getId(), media);
        }
        //this.items.clear();
        //this.items.addAll(medias);
        notifyItemRangeInserted(0, items.size());
    }*/

    public void setCurrentViewType(int currentViewType) {
        this.currentViewType = currentViewType;
    }

    public void setSelectedMedias(LongSparseArray<Media> medias) {
        this.medias = medias;
    }
}
