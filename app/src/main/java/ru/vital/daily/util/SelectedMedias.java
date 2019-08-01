package ru.vital.daily.util;

import android.util.Log;

import androidx.collection.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vital.daily.repository.data.Media;

@Singleton
public class SelectedMedias{

    private final LongSparseArray<Media> selected = new LongSparseArray<>();

    private final LongSparseArray<Media> medias = new LongSparseArray<>();

    @Inject
    public SelectedMedias() {
    }

    public void clear() {
        selected.clear();
        medias.clear();
    }

    public void checkMedia(Media media) {
        if (selected.get(media.getId()) == null) {
            Log.i("my_logs", "mediaPut " + media.getId());
            selected.put(media.getId(), media);
        } else {
            selected.remove(media.getId());
            Log.i("my_logs", "mediaRemove " + media.getId());
        }
    }

    public LongSparseArray<Media> getSelected() {
        return selected;
    }

    public LongSparseArray<Media> getMedias() {
        return medias;
    }

}
