package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.data.Media;

public class MediaListConverter {

    @TypeConverter
    public static List<Media> fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parseList(value, Media.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(List<Media> medias) {
        try {
            return medias == null ? null : LoganSquare.serialize(medias, Media.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
