package ru.vital.daily.repository.converter;

import java.util.Date;

import androidx.room.TypeConverter;

public class DateConverter {

    @TypeConverter
    public static Date fromString(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long fromObject(Date date) {
        return date == null ? null : date.getTime();
    }

}
