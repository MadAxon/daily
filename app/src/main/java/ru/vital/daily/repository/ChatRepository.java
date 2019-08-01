package ru.vital.daily.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.AddMembersRequest;
import ru.vital.daily.repository.api.request.ChatRequest;
import ru.vital.daily.repository.api.request.EmptyJson;
import ru.vital.daily.repository.api.request.IdRequest;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.response.BaseResponse;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.db.ChatDao;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class ChatRepository {

    private final ChatDao chatDao;

    private final Api api;

    @Inject
    public ChatRepository(ChatDao chatDao, Api api) {
        this.chatDao = chatDao;
        this.api = api;
    }

    public void saveChats(List<Chat> chats) {
        DisposableProvider.doCallable(() -> {
            chatDao.insert(chats);
            return true;
        });
    }

    public Single<ItemsResponse<Chat>> getChats(ItemsRequest request) {
        return api.getChats(request);
    }

    public Flowable<ItemsResponse<Chat>> getChats(ItemsRequest request, boolean shouldRetrieveFromDB) {
        if (shouldRetrieveFromDB)
            return Single.concatArray(chatDao.getChats().map(ItemsResponse::new).toSingle(), api.getChats(request).onErrorResumeNext(throwable -> chatDao.getChats().map(ItemsResponse::new).toSingle())).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        else return api.getChats(request).onErrorResumeNext(throwable -> chatDao.getChats().map(ItemsResponse::new).toSingle()).toFlowable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<ItemResponse<Chat>> getChat(IdRequest request) {
        return api.getChat(request).toSingle();
    }

    public Disposable addMembersToChat(AddMembersRequest request, Consumer<Void> onContinue, Consumer<Throwable> error) {
        return DisposableProvider.getDisposableBase(api.addMembersToChat(request),
                s -> {
                    syncChat(request);
                    onContinue.accept(null);
                },
                error);
    }

    public Single<ItemResponse<Chat>> createChat(ItemRequest<ChatSaveModel> request) {
        return api.saveChat(request);
    }

    public Single<BaseResponse<EmptyJson>> removeChat(IdRequest idRequest) {
        return api.removeChat(idRequest);
    }

    public void removeChat(long chatId) {
        DisposableProvider.doCallable(() -> {
            chatDao.delete(chatId);
            return true;
        });
    }

    public Single<ItemResponse<Chat>> getChat(ChatRequest request) {
        if (request.getMemberId() != null)
            return chatDao.getChatByUserId(request.getMemberId())
                    .map(ItemResponse::new)
                    .switchIfEmpty(Maybe.defer(() -> api.getChat(request))).toSingle();
        else return chatDao.getChat(request.getId())
                .map(ItemResponse::new)
                .switchIfEmpty(Maybe.defer(() -> api.getChat(request))).toSingle();
    }

    public void saveChat(Chat chat) {
        DisposableProvider.doCallable(() -> {
            chatDao.insert(chat);
            return true;
        });
    }

    public Observable<Long> saveEmptyChat(Chat chat) {
        return DisposableProvider.getCallable(() -> chatDao.insertEmpty(chat));
    }

    public void updateChat(Chat chat) {
        DisposableProvider.doCallable(() -> {
            chatDao.update(chat);
            return true;
        });
    }

    private void syncChat(IdRequest request) {
        DisposableProvider.getDisposableItem(getChat(request),
                this::updateChat, throwable -> {

                });
    }
}
