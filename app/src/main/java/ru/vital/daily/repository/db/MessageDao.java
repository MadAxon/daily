package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;
import ru.vital.daily.repository.data.Message;

@Dao
public interface MessageDao {

    @Query("SELECT * from messages where chatId = :chatId")
    Maybe<List<Message>> getMessages(long chatId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Message> messages);

}
