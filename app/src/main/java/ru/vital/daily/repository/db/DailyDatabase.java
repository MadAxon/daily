package ru.vital.daily.repository.db;

import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import ru.vital.daily.repository.converter.ChatInfoConverter;
import ru.vital.daily.repository.converter.DateConverter;
import ru.vital.daily.repository.converter.MediaConverter;
import ru.vital.daily.repository.converter.MediaListConverter;
import ru.vital.daily.repository.converter.MediaModelListConverter;
import ru.vital.daily.repository.converter.MessageContentConverter;
import ru.vital.daily.repository.converter.MessageConverter;
import ru.vital.daily.repository.converter.MessageInfoConverter;
import ru.vital.daily.repository.converter.RelationConverter;
import ru.vital.daily.repository.converter.SocialConverter;
import ru.vital.daily.repository.converter.UserConverter;
import ru.vital.daily.repository.converter.UserListConverter;
import ru.vital.daily.repository.data.Action;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.MessageContentModel;

@androidx.room.Database(entities = {
        User.class,
        Key.class,
        Chat.class,
        Message.class,
        Action.class,
        Draft.class
},
        version = 1,
        exportSchema = false)
@TypeConverters({
        DateConverter.class,
        RelationConverter.class,
        SocialConverter.class,
        MediaConverter.class,
        ChatInfoConverter.class,
        UserListConverter.class,
        MediaModelListConverter.class,
        MediaListConverter.class,
        MessageInfoConverter.class,
        UserConverter.class,
        MessageConverter.class,
        MessageContentConverter.class
})
public abstract class DailyDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract KeyDao keyDao();

    public abstract ChatDao chatDao();

    public abstract MessageDao messageDao();

    public abstract ActionDao actionDao();

    public abstract DraftDao draftDao();

}
