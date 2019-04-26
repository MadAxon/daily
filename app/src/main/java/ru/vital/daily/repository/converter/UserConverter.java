package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.data.User;

public class UserConverter {

    @TypeConverter
    public static User fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parse(value, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(User user) {
        try {
            return user == null ? null : LoganSquare.serialize(user);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
