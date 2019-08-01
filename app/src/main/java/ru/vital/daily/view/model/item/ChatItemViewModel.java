package ru.vital.daily.view.model.item;

import androidx.databinding.Observable;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import ru.vital.daily.BR;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.util.StaticData;

public class ChatItemViewModel extends ItemViewModel<Chat> {

    private SingleLiveEvent<Long> updateChatEvent;

    private User user;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final Subject<Boolean> typingSubject = PublishSubject.create(),
            updateChatSubject = PublishSubject.create();

    public ChatItemViewModel() {
        compositeDisposable.add(typingSubject
                .filter(aBoolean -> aBoolean)
                .debounce(1100, TimeUnit.MILLISECONDS)
                .subscribe(aBoolean -> item.setTyping(!aBoolean)));
        compositeDisposable.add(updateChatSubject
                .filter(aBoolean -> aBoolean)
                .debounce(1, TimeUnit.SECONDS)
                .subscribe(aBoolean -> {
                    item.setUpdate(false);
                    updateChatEvent.postValue(item.getId());
                }));
    }

    @Override
    public void setItem(Chat item) {
        super.setItem(item);
        if (ChatType.dialog.name().equals(item.getType()) && item.getMembers() != null)
            for (User user : item.getMembers()) {
                if (user.getId() != StaticData.getData().profile.getId()) {
                    this.user = user;
                    break;
                }
            }
        item.addOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void unbind() {
        item.removeOnPropertyChangedCallback(onPropertyChangedCallback);
        compositeDisposable.clear();
        super.unbind();
    }

    private final OnPropertyChangedCallback onPropertyChangedCallback = new OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
                case BR.typing:
                    typingSubject.onNext(item.getTyping());
                    break;
                case BR.update:
                    updateChatSubject.onNext(item.getUpdate());
                    break;
            }
        }
    };

    public void setUpdateChatEvent(SingleLiveEvent<Long> updateChatEvent) {
        this.updateChatEvent = updateChatEvent;
    }
}
