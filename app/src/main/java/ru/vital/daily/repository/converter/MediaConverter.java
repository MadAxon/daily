package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.data.Media;

public class MediaConverter {

    @TypeConverter
    public static Media fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parse(value, Media.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(Media media) {
        try {
            return media == null ? null : LoganSquare.serialize(media);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
