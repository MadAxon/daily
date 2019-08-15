package ru.vital.daily.view.model;

import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.api.response.SocketResponse;

public class ChatSelectorViewModel extends ViewModel {

    public final SingleLiveEvent<Void> mainShowEvent = new SingleLiveEvent<>();

    public final MutableLiveData<Long> updateChatEvent = new MutableLiveData<>();

    public final MutableLiveData<SocketResponse> readMessageEvent = new MutableLiveData<>();

    public final MutableLiveData<Long> typingSocketEvent = new MutableLiveData<>();

    private final Subject<Long> updateChatSubject = PublishSubject.create();

    @Inject
    public ChatSelectorViewModel() {
        compositeDisposable.add(updateChatSubject
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(updateChatEvent::postValue));
    }

    public void setChatIdForUpdating(Long chatId) {
        updateChatSubject.onNext(chatId);
    }

}
