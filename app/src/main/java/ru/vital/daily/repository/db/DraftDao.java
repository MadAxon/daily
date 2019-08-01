package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;
import io.reactivex.Single;
import ru.vital.daily.repository.data.Draft;

@Dao
public interface DraftDao {

    @Query("DELETE from drafts where id = :id")
    void delete(long id);

    @Query("SELECT * from drafts where id = :id")
    Single<Draft> get(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Draft draft);

    @Query("SELECT * from drafts")
    Single<List<Draft>> get();

}
