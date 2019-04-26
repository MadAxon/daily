package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.model.ChatInfoModel;
import ru.vital.daily.repository.model.RelationModel;

public class ChatInfoConverter {

    @TypeConverter
    public static ChatInfoModel fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parse(value, ChatInfoModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(ChatInfoModel chatInfoModel) {
        try {
            return chatInfoModel == null ? null : LoganSquare.serialize(chatInfoModel);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
