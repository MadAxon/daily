package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.model.RelationModel;
import ru.vital.daily.repository.model.SocialModel;

public class RelationConverter {

    @TypeConverter
    public static RelationModel fromString(String value) throws IOException {
        return value == null ? null : LoganSquare.parse(value, RelationModel.class);
    }

    @TypeConverter
    public static String fromObject(RelationModel relationModel) throws IOException {
        return relationModel == null ? null : LoganSquare.serialize(relationModel);
    }

}
