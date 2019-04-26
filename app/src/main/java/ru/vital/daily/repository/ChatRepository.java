package ru.vital.daily.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.AddMembersRequest;
import ru.vital.daily.repository.api.request.GetChatRequest;
import ru.vital.daily.repository.api.request.IdRequest;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.response.ItemResponse;
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

    public Single<ItemResponse<Chat>> getChat(GetChatRequest request) {
        return chatDao.getChatByUserId(request.getMemberId())
                .map(ItemResponse::new)
                .switchIfEmpty(Maybe.defer(() -> api.getChat(request))).toSingle();
    }

    public void saveChat(Chat chat) {
        DisposableProvider.doCallable(() -> {
            chatDao.insert(chat);
            return null;
        });
    }

    public void updateChat(Chat chat) {
        DisposableProvider.doCallable(() -> {
            chatDao.update(chat);
            return null;
        });
    }

    private void syncChat(IdRequest request) {
        DisposableProvider.getDisposableItem(getChat(request),
                this::updateChat, throwable -> {

                });
    }
}
