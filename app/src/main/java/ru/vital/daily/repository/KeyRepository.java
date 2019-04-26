package ru.vital.daily.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.EmailRequest;
import ru.vital.daily.repository.api.request.PhoneRequest;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.db.KeyDao;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class KeyRepository {

    private final KeyDao keyDao;

    private final Api api;

    @Inject
    public KeyRepository(KeyDao keyDao, Api api) {
        this.keyDao = keyDao;
        this.api = api;
    }

    public Single<ItemResponse<Key>> getCurrentKey() {
        return keyDao.getCurrentKey().map(ItemResponse::new);
    }

    public Single<ItemResponse<Key>> retrieveNewKey(PhoneRequest phoneRequest) {
        return api.signInPhone(phoneRequest);
    }

    public Single<ItemResponse<Key>> retrieveNewKey(EmailRequest emailRequest) {
        return api.signInEmail(emailRequest);
    }

    public void insertKey(Key key) {
        DisposableProvider.doCallable(() -> {
            keyDao.insert(key);
            return null;
        });
    }

    public void updateCurrentUserId(long userId) {
        DisposableProvider.doCallable(() -> {
            keyDao.updateCurrentUserId(userId);
            return null;
        });
    }

    public void clearKeys() {
        DisposableProvider.doCallable(() -> {
            keyDao.deleteAll();
            return null;
        });
    }
}
