package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.model.SocialModel;

public class SocialConverter {

    @TypeConverter
    public static SocialModel fromString(String value) throws IOException {
        return value == null ? null : LoganSquare.parse(value, SocialModel.class);
    }

    @TypeConverter
    public static String fromObject(SocialModel socialModel) throws IOException {
        return socialModel == null ? null : LoganSquare.serialize(socialModel);
    }

}
