package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.model.MediaModel;

public class MediaModelListConverter {

    @TypeConverter
    public static List<MediaModel> fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parseList(value, MediaModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(List<MediaModel> medias) {
        try {
            return medias == null ? null : LoganSquare.serialize(medias, MediaModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
