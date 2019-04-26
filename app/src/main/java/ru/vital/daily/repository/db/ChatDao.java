package ru.vital.daily.repository.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;
import ru.vital.daily.repository.data.Chat;

@Dao
public interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Chat chat);

    @Query("SELECT * from chats where id = :id")
    Maybe<Chat> getChat(long id);

    @Query("SELECT * from chats where userId = :userId")
    Maybe<Chat> getChatByUserId(long userId);

    @Update
    void update(Chat chat);

}
