package ru.vital.daily.repository.converter;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.List;

import androidx.room.TypeConverter;
import ru.vital.daily.repository.data.User;

public class UserListConverter {

    @TypeConverter
    public static List<User> fromString(String value) {
        try {
            return value == null ? null : LoganSquare.parseList(value, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String fromObject(List<User> users) {
        try {
            return users == null ? null : LoganSquare.serialize(users, User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
