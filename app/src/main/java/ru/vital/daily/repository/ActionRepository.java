package ru.vital.daily.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.Action;
import ru.vital.daily.repository.db.ActionDao;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class ActionRepository {

    private final ActionDao actionDao;

    @Inject
    public ActionRepository(ActionDao actionDao) {
        this.actionDao = actionDao;
    }

    public Single<ItemResponse<Action>> getAction(long id) {
        return actionDao.getAction(id).map(ItemResponse::new);
    }

    public Single<ItemsResponse<Action>> getActions() {
        return actionDao.getActions().map(ItemsResponse::new).toSingle();
    }

    public Observable<Integer> updateAction(long messageId, long chatId, String operation) {
        return DisposableProvider.getCallable(() -> actionDao.update(messageId, chatId, operation));
    }

    public Observable<Long> insertAction(Action job) {
        return DisposableProvider.getCallable(() -> actionDao.insert(job));
    }

    public Observable<Integer> deleteAction(long messageId, long chatId) {
        return DisposableProvider.getCallable(() -> {
            return actionDao.delete(messageId, chatId);
        });
    }

    public void deleteAction(String messageIds, long chatId) {
        DisposableProvider.doCallable(() -> {
            actionDao.delete(messageIds, chatId);
            return true;
        });
    }

    public void deleteAction(long id) {
        DisposableProvider.doCallable(() -> {
            actionDao.delete(id);
            return true;
        });
    }

    public void deleteDoubledActions(long id, long messageId, long chatId) {
        DisposableProvider.doCallable(() -> {
            actionDao.deleteDoubledActions(id, messageId, chatId);
            return true;
        });
    }

    public void deleteDoubledActions(long id, String messageIds, long chatId) {
        DisposableProvider.doCallable(() -> {
            actionDao.deleteDoubledActions(id, messageIds, chatId);
            return true;
        });
    }
}
