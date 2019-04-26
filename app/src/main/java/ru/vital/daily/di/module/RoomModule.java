package ru.vital.daily.di.module;

import android.app.Application;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import ru.vital.daily.repository.db.ChatDao;
import ru.vital.daily.repository.db.DailyDatabase;
import ru.vital.daily.repository.db.KeyDao;
import ru.vital.daily.repository.db.MediaDao;
import ru.vital.daily.repository.db.MessageDao;
import ru.vital.daily.repository.db.UserDao;

@Module
public class RoomModule {

    @Singleton
    @Provides
    public DailyDatabase provideDailyDatabase(Application application) {
        return Room.databaseBuilder(application, DailyDatabase.class, "daily_database").build();
    }

    @Singleton
    @Provides
    public UserDao provideUserDao(DailyDatabase dailyDatabase) {
        return dailyDatabase.userDao();
    }

    @Singleton
    @Provides
    public KeyDao provideKeyDao(DailyDatabase dailyDatabase) {
        return dailyDatabase.keyDao();
    }

    @Singleton
    @Provides
    public ChatDao provideChatDao(DailyDatabase dailyDatabase) {
        return dailyDatabase.chatDao();
    }

    @Singleton
    @Provides
    public MediaDao provideMediaDao(DailyDatabase dailyDatabase) {
        return dailyDatabase.mediaDao();
    }

    @Singleton
    @Provides
    public MessageDao provideMessageDao(DailyDatabase dailyDatabase) {
        return dailyDatabase.messageDao();
    }
}
