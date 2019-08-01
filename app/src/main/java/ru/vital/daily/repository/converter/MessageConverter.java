package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.data.Message;

public class MessageConverter {

    @TypeConverter
    public static Message fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parse(value, Message.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(Message Message) {
        try {
            return Message == null ? null : LoganSquare.serialize(Message);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
