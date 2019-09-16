package ru.vital.daily.repository.db;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;

@Dao
public interface MessageDao {

    @Query("SELECT * from messages where chatId = :chatId ORDER BY createdAt DESC limit :offset, :size")
    Maybe<List<Message>> getMessages(long chatId, int offset, int size);

    @Query("SELECT * from messages")
    Maybe<List<Message>> getMessages();

    @Query("SELECT * from messages where id in (:ids) and chatId = :chatId and shouldSync = :shouldSync")
    Maybe<List<Message>> getMessages(long[] ids, long chatId, boolean shouldSync);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Message> messages);

    @Query("DELETE from messages where createdAt between 0 and :endDate")
    void deleteExpired(long endDate);

    @Update
    void update(List<Message> messages);

    @Query("UPDATE messages SET checkedAt = :checkedAt where id = :id and chatId = :chatId")
    void updateCheckedAt(long id, long chatId, long checkedAt);

    @Query("UPDATE messages SET updatedAt = :updatedAt where id = :id and chatId = :chatId and shouldSync = :shouldSync")
    void updateUpdatedAt(long id, long chatId, long updatedAt, boolean shouldSync);

    @Query("UPDATE messages SET sendStatus = :sendStatus where id = :id and chatId = :chatId")
    void updateSendStatus(long id, long chatId, Boolean sendStatus);

    @Query("UPDATE messages SET text = :text and medias = :medias and updatedAt = :updatedAt where id = :id and chatId = :chatId")
    int updateMessage(long id, long chatId, String text, String medias, Date updatedAt);

    @Query("UPDATE messages SET text = :text, updatedAt = :updatedAt where id = :id and chatId = :chatId")
    int updateMessage(long id, long chatId, String text, Date updatedAt);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Message message);

    @Query("SELECT * from messages where id = :id and chatId = :chatId")
    Single<Message> getMessage(long id, long chatId);

    @Query("SELECT * from messages where id = :id and chatId = :chatId and shouldSync = :shouldSync")
    Single<Message> getMessage(long id, long chatId, boolean shouldSync);

    @Query("UPDATE messages SET gridHeight = :height where id = :id and chatId = :chatId and shouldSync = :shouldSync")
    void updateHeight(long id, long chatId, boolean shouldSync, int height);

    @Delete
    int delete(Message message);

    @Query("DELETE from messages where id = :id and chatId = :chatId")
    int delete(long id, long chatId);

    @Query("DELETE from messages where id in (:ids) and chatId = :chatId")
    int delete(long[] ids, long chatId);

    @Query("UPDATE messages SET medias = :medias where id = :id and chatId = :chatId and shouldSync = :shouldSync")
    void updateMedias(long id, long chatId, String medias, boolean shouldSync);

    @Query("UPDATE messages SET readAt = :readAt where id = :id and chatId = :chatId and shouldSync = 0")
    void updateReadAt(long id, long chatId, Date readAt);

    @Query("SELECT COUNT(id) from messages where id >= :id and chatId = :chatId")
    int findMessageIndex(long id, long chatId);

}
