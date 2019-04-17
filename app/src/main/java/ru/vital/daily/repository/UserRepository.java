package ru.vital.daily.repository;

import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.db.UserDao;

@Singleton
public class UserRepository {

    private final Api api;

    private final UserDao userDao;

    @Inject
    public UserRepository(Api api, UserDao userDao) {
        this.api = api;
        this.userDao = userDao;
    }

    public void insertUserToDB(User user) {
        Observable.fromCallable((Callable<Void>) () -> {
            userDao.insert(user);
            return null;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe();
    }


}
