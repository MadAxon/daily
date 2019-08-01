package ru.vital.daily.util;

import androidx.collection.LongSparseArray;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vital.daily.repository.data.Media;

@Singleton
public class MediaProgressHelper {

    private final LongSparseArray<Media> medias = new LongSparseArray<>();

    @Inject
    public MediaProgressHelper() {
    }

    public Media getMedia(long key) {
        return medias.get(key);
    }

    public void putMedia(Media media) {
        medias.put(media.getId(), media);
    }

    public boolean contains(long key) {
        return medias.containsKey(key);
    }

    public void clear() {
        medias.clear();
    }

    public void remove(long key) {
        medias.remove(key);
    }
}
