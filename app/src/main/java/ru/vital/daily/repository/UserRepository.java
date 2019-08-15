package ru.vital.daily.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.EmptyJson;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.db.UserDao;
import ru.vital.daily.repository.model.ProfileSaveModel;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class UserRepository {

    private final Api api;

    private final UserDao userDao;

    @Inject
    public UserRepository(Api api, UserDao userDao) {
        this.api = api;
        this.userDao = userDao;
    }

    public Single<ItemsResponse<User>> getUsers() {
        return userDao.getUsers().map(ItemsResponse::new);
    }

    public Flowable<ItemsResponse<User>> getUsers(long id, ItemsRequest request) {
        return Single.concatArray(userDao.getUsersExceptMineByDate(id).map(ItemsResponse::new).toSingle(), api.getUsers(request)).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ItemsResponse<User>> getUsersFromApi(ItemsRequest request) {
        return api.getUsers(request);
    }

    public Single<ItemResponse<User>> getProfile(long userId) {
        return userDao.getUser(userId)
                .map(ItemResponse::new)
                .switchIfEmpty(Maybe.defer(() -> api.getProfile(new EmptyJson()))).toSingle().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<ItemResponse<User>> saveProfile(ItemRequest<ProfileSaveModel> request) {
        return api.saveProfile(request);
    }

    public void saveUser(User user) {
        DisposableProvider.doCallable(() -> {
            userDao.insert(user);
            return true;
        });
        /*Observable.fromCallable((Callable<Void>) () -> {
            userDao.insert(user);
            return null;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe();*/
    }


}
