package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.vital.daily.repository.data.Chat;

@Dao
public interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Chat chat);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertEmpty(Chat chat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Chat> chats);

    @Query("SELECT * from chats where id = :id")
    Maybe<Chat> getChat(long id);

    @Query("SELECT * from chats where userId = :userId")
    Maybe<Chat> getChatByUserId(long userId);

    @Query("DELETE from chats where id = :id")
    void delete(long id);

    @Update
    void update(Chat chat);

    @Query("SELECT * from chats order by messagingAt desc, id desc")
    Maybe<List<Chat>> getChats();

}
