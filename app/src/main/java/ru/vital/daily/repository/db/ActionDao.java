package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.vital.daily.repository.data.Action;

@Dao
public interface ActionDao {

    @Query("SELECT * from actions where id = :id")
    Single<Action> getAction(long id);

    @Query("SELECT * from actions")
    Maybe<List<Action>> getActions();

    @Query("DELETE from actions where id = :id")
    void delete(long id);

    @Query("DELETE from actions where messageId = :messageId and chatId = :chatId")
    int delete(long messageId, long chatId);

    @Query("DELETE from actions where messageIds = :messageIds and chatId = :chatId")
    void delete(String messageIds, long chatId);

    @Query("UPDATE actions SET `action` = :action where messageId = :messageId and chatId = :chatId")
    int update(long messageId, long chatId, String action);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Action action);

    @Query("DELETE from actions where id <> :id and messageId = :messageId and chatId = :chatId")
    void deleteDoubledActions(long id, long messageId, long chatId);

    @Query("DELETE from actions where id <> :id and messageIds = :messageIds and chatId = :chatId")
    void deleteDoubledActions(long id, String messageIds, long chatId);

}
