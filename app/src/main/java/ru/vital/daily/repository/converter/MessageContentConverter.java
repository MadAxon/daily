package ru.vital.daily.repository.converter;

import androidx.room.TypeConverter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import ru.vital.daily.repository.model.MessageContentModel;
import ru.vital.daily.repository.model.MessageInfoModel;

public class MessageContentConverter {

    @TypeConverter
    public static MessageContentModel fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parse(value, MessageContentModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(MessageContentModel messageContentModel) {
        try {
            return messageContentModel == null ? null : LoganSquare.serialize(messageContentModel);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
