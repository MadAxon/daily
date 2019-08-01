package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;
import ru.vital.daily.repository.data.Key;

@Dao
public interface KeyDao {

    @Query("SELECT * from keys")
    Single<List<Key>> getKeys();

    @Query("SELECT * from keys where isCurrent = 1")
    Single<Key> getCurrentKey();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Key key);

    @Query("UPDATE keys SET userId = :userId where isCurrent = 1")
    int updateCurrentUserId(long userId);

    @Query("DELETE from keys")
    void deleteAll();

    @Query("DELETE from keys where userId = :userId")
    void delete(long userId);
}
