package ru.vital.daily.view.model;

import android.app.Application;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.MessageRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.SocketResponse;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.util.ChatData;
import ru.vital.daily.util.DisposableProvider;

public class MainViewModel extends ViewModel {

    public final String authFragmentTag = "authFragmentTag",
            feedFragmentTag = "feedFragmentTag",
            homeFragmentTag = "homeFragmentTag",
            accountFragmentTag = "accountFragmentTag",
            registerFragmentTag = "registerFragmentTag";

    public final SingleLiveEvent<Void> mainShowEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Long> updateChatEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<SocketResponse> readMessageEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<Long> typingSocketEvent = new SingleLiveEvent<>();

    public MutableLiveData<Boolean> isOnline;

    private final KeyRepository keyRepository;

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;

    private final Subject<Long> updateChatSubject = PublishSubject.create();

    private final ChatData chatData;

    @Inject
    public MainViewModel(KeyRepository keyRepository, UserRepository userRepository, ChatData chatData, ChatRepository chatRepository, MessageRepository messageRepository) {
        this.keyRepository = keyRepository;
        this.userRepository = userRepository;
        this.chatData = chatData;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        /*Key key = new Key();
        key.setAccessKey("0toK2Yi7PUXWOCMWDGBQumShSNkYnw6SjsYFaOyY38VSrSxelXzySXdnnqayGFQ6");
        key.setUserId(7);
        keyRepository.saveKey(key);*/
        compositeDisposable.add(updateChatSubject
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(updateChatEvent::postValue));
    }

    public void initChats() {
        DisposableProvider.getDisposableItems(chatRepository.getChats(), chats -> {
            for (Chat chat : chats)
                DisposableProvider.getDisposableItems(messageRepository.getMessages(chat.getId()),
                        messages -> {
                            chatData.items.put(chat.getId(), messages);
                        }, throwable -> {

                        });
        }, throwable -> {

        });
    }

    public Single<ItemResponse<Key>> getKeys() {
        return keyRepository.getCurrentKey();
    }

    public Single<ItemResponse<User>> getProfile(long userId) {
        return userRepository.getProfile(userId);
    }

    public void setChatIdForUpdating(Long chatId) {
        updateChatSubject.onNext(chatId);
    }
}
