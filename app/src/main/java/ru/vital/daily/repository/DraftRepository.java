package ru.vital.daily.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.db.DraftDao;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class DraftRepository {

    private final DraftDao draftDao;

    @Inject
    public DraftRepository(DraftDao draftDao) {
        this.draftDao = draftDao;
    }

    public Single<ItemResponse<Draft>> get(long id) {
        return draftDao.get(id).map(ItemResponse::new);
    }

    public Single<ItemsResponse<Draft>> get() {
        return draftDao.get().map(ItemsResponse::new);
    }

    public void delete(long id) {
        DisposableProvider.doCallable(() -> {
            draftDao.delete(id);
            return true;
        });
    }

    public void insert(Draft draft) {
        DisposableProvider.doCallable(() -> {
            draftDao.insert(draft);
            return true;
        });
    }
}
