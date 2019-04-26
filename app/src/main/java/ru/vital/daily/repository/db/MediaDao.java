package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Maybe;
import ru.vital.daily.repository.data.Media;

@Dao
public interface MediaDao {

    @Query("SELECT * from medias")
    Maybe<List<Media>> getMedias();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Media media);

    @Delete
    void delete(Media media);

}
