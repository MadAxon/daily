package ru.vital.daily.repository.db;

import androidx.room.RoomDatabase;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;

@androidx.room.Database(entities = {User.class, Key.class}, version = 1, exportSchema = false)
public abstract class DailyDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract KeyDao keyDao();

}
