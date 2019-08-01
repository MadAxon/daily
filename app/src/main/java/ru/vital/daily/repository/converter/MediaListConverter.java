package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.collection.LongSparseArray;
import androidx.room.TypeConverter;

import ru.vital.daily.repository.data.Media;

public class MediaListConverter {

    @TypeConverter
    public static LongSparseArray<Media> fromString(String value) {
        List<Media> medias = null;
        if (value != null)
            try {
                medias = LoganSquare.parseList(value, Media.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (medias != null) {
            LongSparseArray<Media> mediaSparseArray = new LongSparseArray<>();
            for (Media media : medias)
                mediaSparseArray.put(media.getId(), media);
            return mediaSparseArray;
        }
        return null;
    }

    @TypeConverter
    public static String fromObject(LongSparseArray<Media> mediaSparseArray) {
        final int size = mediaSparseArray != null ? mediaSparseArray.size() : 0;
        if (size > 0) {
            List<Media> medias = new ArrayList<>(size);
            for (int i = 0; i < size; i++)
                medias.add(mediaSparseArray.valueAt(i));
            try {
                return LoganSquare.serialize(medias, Media.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
