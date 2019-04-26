package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.model.MessageInfoModel;

public class MessageInfoConverter {

    @TypeConverter
    public static MessageInfoModel fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parse(value, MessageInfoModel.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(MessageInfoModel messageInfoModel) {
        try {
            return messageInfoModel == null ? null : LoganSquare.serialize(messageInfoModel);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
