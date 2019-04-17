package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Single;
import ru.vital.daily.repository.data.Key;

@Dao
public interface KeyDao {

    @Query("SELECT * from keys")
    Single<List<Key>> getKeys();

    @Query("SELECT * from keys where accessKey = :accessKey")
    Single<List<Key>> getKey(String accessKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Key key);

    @Delete
    void delete(Key key);

    @Delete
    void delete(List<Key> keys);
}
