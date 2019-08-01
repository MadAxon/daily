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
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.SocketResponse;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;

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

    private final Subject<Long> updateChatSubject = PublishSubject.create();

    @Inject
    public MainViewModel(KeyRepository keyRepository, UserRepository userRepository) {
        this.keyRepository = keyRepository;
        this.userRepository = userRepository;
        /*Key key = new Key();
        key.setAccessKey("0toK2Yi7PUXWOCMWDGBQumShSNkYnw6SjsYFaOyY38VSrSxelXzySXdnnqayGFQ6");
        key.setUserId(7);
        keyRepository.saveKey(key);*/
        compositeDisposable.add(updateChatSubject
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(updateChatEvent::postValue));
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
