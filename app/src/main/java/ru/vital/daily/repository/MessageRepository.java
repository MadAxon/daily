package ru.vital.daily.repository;

import androidx.collection.LongSparseArray;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.EmptyJson;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.request.MessageForwardRequest;
import ru.vital.daily.repository.api.request.MessageReadRequest;
import ru.vital.daily.repository.api.request.MessageRemoveRequest;
import ru.vital.daily.repository.api.request.MessageRequest;
import ru.vital.daily.repository.api.request.MessagesRequest;
import ru.vital.daily.repository.api.response.BaseResponse;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.db.MessageDao;
import ru.vital.daily.repository.model.MessageSendModel;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class MessageRepository {

    private final Api api;

    private final MessageDao messageDao;

    @Inject
    public MessageRepository(Api api, MessageDao messageDao) {
        this.api = api;
        this.messageDao = messageDao;
    }

    public Single<ItemsResponse<Message>> getMessages() {
        return messageDao.getMessages().toSingle().map(ItemsResponse::new);
    }

    public Single<ItemsResponse<Message>> getMessages(long[] ids, long chatId) {
        return messageDao.getMessages(ids, chatId, true).toSingle().map(ItemsResponse::new);
    }

    public Flowable<ItemsResponse<Message>> getMessages(MessagesRequest request) {
        return api.getMessages(request).toFlowable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /*public Flowable<ItemsResponse<Message>> getMessages(MessagesRequest request) {
        return Single.concatArray(messageDao.getMessages(request.getChatId(), 0).map(ItemsResponse::new).toSingle(), api.getMessages(request).onErrorResumeNext(throwable -> messageDao.getMessages(request.getChatId(), 0).map(ItemsResponse::new).toSingle())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }*/

    public Single<ItemsResponse<Message>> getMessages(long[] ids, Long chatId) {
        return api.getMessages(new MessagesRequest(ids, chatId));
    }

    public Single<ItemsResponse<Message>> getMessages(Long chatId, int pageSize) {
        return api.getMessages(new MessagesRequest(chatId, pageSize));
    }

    public Single<ItemsResponse<Message>> getMoreMessages(MessagesRequest request) {
        return messageDao.getMessages(request.getChatId(), request.getPageIndex() * request.getPageSize())
                .map(ItemsResponse::new)
                .switchIfEmpty(Maybe.defer(() -> api.getMessages(request).toMaybe())).toSingle();
    }

    public void saveMessages(List<Message> messages, long checkedAt) {
        DisposableProvider.doCallable(() -> {
            for (Message message : messages)
                message.setCheckedAt(checkedAt);
            messageDao.insert(messages);
            return true;
        });
    }

    public void updateExpiredMessages(List<Message> messages, long checkedAt) {
        DisposableProvider.doCallable(() -> {
            for (Message message : messages)
                messageDao.updateCheckedAt(message.getId(), message.getChatId(), checkedAt);
            return true;
        });
    }

    public void updateSendStatus(Message message) {
        DisposableProvider.doCallable(() -> {
            messageDao.updateSendStatus(message.getId(), message.getChatId(), message.getSendStatus());
            return true;
        });
    }

    public void clearExpiredMessages(long endDate) {
        DisposableProvider.doCallable(() -> {
            messageDao.deleteExpired(endDate);
            return true;
        });
    }

    public Single<ItemResponse<Message>> sendMessage(ItemRequest<MessageSendModel> request) {
        return api.sendMessage(request);
    }

    public Observable<Long> saveMessage(Message message) {
        return DisposableProvider.getCallable(() -> messageDao.insert(message));
    }

    public Observable<Integer> updateMessage(Message message) {
        if (message.getMedias() != null && message.getMedias().size() > 0)
            return DisposableProvider.getCallable(() -> {
                LongSparseArray<Media> mediaSparseArray = message.getMedias();
                final int size = mediaSparseArray.size();
                List<Media> medias = new ArrayList<>(size);
                for (int i = 0; i < size; i++)
                    medias.add(mediaSparseArray.valueAt(i));
                return messageDao.updateMessage(message.getId(), message.getChatId(), message.getText(), LoganSquare.serialize(medias, Media.class), message.getUpdatedAt());
            });
        else
            return DisposableProvider.getCallable(() -> messageDao.updateMessage(message.getId(), message.getChatId(), message.getText(), message.getUpdatedAt()));
    }

    public Single<ItemResponse<Message>> getMessage(long id, long chatId) {
        return messageDao.getMessage(id, chatId).map(ItemResponse::new);
    }

    public Single<ItemResponse<Message>> getMessage(long id, long chatId, boolean shouldSync) {
        return messageDao.getMessage(id, chatId, shouldSync).map(ItemResponse::new);
    }

    public Single<ItemResponse<Message>> getMessage(MessageRequest messageRequest) {
        return api.getMessage(messageRequest);
    }

    public Observable<Integer> deleteMessage(Message message) {
        return DisposableProvider.getCallable(() -> messageDao.delete(message));
    }

    public Observable<Integer> deleteMessage(long id, long chatId) {
        return DisposableProvider.getCallable(() -> messageDao.delete(id, chatId));
    }

    public Single<BaseResponse<EmptyJson>> deleteMessages(MessageRemoveRequest request) {
        return api.deleteMessages(request);
    }

    public Observable<Integer> deleteMessages(long[] ids, long chatId) {
        return DisposableProvider.getCallable(() -> messageDao.delete(ids, chatId));
    }

    public void updateMedias(long id, long chatId, LongSparseArray<Media> mediaSparseArray, boolean shouldSync) {
        DisposableProvider.doCallable(() -> {
            final int size = mediaSparseArray.size();
            List<Media> medias = new ArrayList<>(size);
            for (int i = 0; i < size; i++)
                medias.add(mediaSparseArray.valueAt(i));
            try {
                messageDao.updateMedias(id, chatId, LoganSquare.serialize(medias, Media.class), shouldSync);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public Observable<Single<ItemResponse<long[]>>> readMessages(List<Message> unreadMessages, long chatId) {
        return DisposableProvider.getCallable(() -> {
            int size = unreadMessages.size();
            long[] ids = new long[size];
            for (int i = 0; i < size; i++)
                ids[i] = unreadMessages.get(i).getId();
            return api.readMessage(new MessageReadRequest(ids, chatId)).map(emptyJsonBaseResponse -> new ItemResponse<>(ids));
        });
    }

    public Single<ItemsResponse<Message>> forwardMessages(long[] messagesIds, long fromChatId, long toChatId) {
        return api.forwardMessages(new MessageForwardRequest(messagesIds, fromChatId, toChatId));
    }

    public void updateMessageUpdatedAt(long id, long chatId, Date updatedAt) {
        DisposableProvider.doCallable(() -> {
            messageDao.updateUpdatedAt(id, chatId, updatedAt.getTime(), false);
            return true;
        });
    }
}
