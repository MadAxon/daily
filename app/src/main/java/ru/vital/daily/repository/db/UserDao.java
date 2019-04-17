package ru.vital.daily.repository.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Single;
import ru.vital.daily.repository.data.User;

@Dao
public interface UserDao {

    @Query("SELECT * from users")
    Single<List<User>> getUsers();

    @Query("SELECT * from users where id = :id")
    Single<List<User>> getUser(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

}
