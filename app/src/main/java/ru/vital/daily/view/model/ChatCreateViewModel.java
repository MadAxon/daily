package ru.vital.daily.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.subjects.PublishSubject;
import ru.vital.daily.BR;
import ru.vital.daily.enums.Direction;
import ru.vital.daily.enums.OrderBy;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.User;

public class ChatCreateViewModel extends ViewModel {

    public final String chatCreateFragmentTag = "chatCreateFragmentTag",
                    groupCreateFragmentTag = "groupCreateFragmentTag";

    public final SingleLiveEvent<Void> createChannelClickEvent = new SingleLiveEvent<>(),
                                    createGroupClickEvent = new SingleLiveEvent<>(),
                                    clearAdapterEvent = new SingleLiveEvent<>();

    public final ItemsRequest request;

    public final UserRepository userRepository;

    public final MutableLiveData<List<User>> users = new MutableLiveData<>();

    private final PublishSubject<String> searchPublish = PublishSubject.create();

    @Inject
    public ChatCreateViewModel(UserRepository userRepository, ItemsRequest request) {
        this.userRepository = userRepository;
        request.setDirection(Direction.desc.name());
        request.setOrderBy(OrderBy.onlineAt.name());
        request.setPageIndex(0);
        request.setPageSize(20);
        this.request = request;
        this.request.addOnPropertyChangedCallback(onPropertyChangedCallback);

        compositeDisposable.add(searchPublish
                .debounce(1, TimeUnit.SECONDS)
                .switchMap(string -> {
                    //request.setSearchText(string);
                    return userRepository.getUsersFromApi(request).toObservable();
                })
                .subscribe(new ItemsResponseHandler<>(userList -> {
                    users.postValue(userList);
                    isLoading.set(false);
                }, throwable -> {
                    errorEvent.postValue(throwable);
                    isLoading.set(false);
                }), throwable -> {
                    errorEvent.postValue(throwable);
                    isLoading.set(false);
                }));

        isLoading.set(true);
        searchPublish.onNext("");
    }

    public void loadMore() {
        request.setPageIndex(request.getPageIndex() + 1);
        searchPublish.onNext(request.getSearchText());
    }

    public void onCreateChannelClicked() {
        createChannelClickEvent.call();
    }

    public void onCreateGroupClickEvent() {
        createGroupClickEvent.call();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        request.removeOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    private Observable.OnPropertyChangedCallback onPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
                case BR.searchText:
                    if (!isLoading.get()) isLoading.set(true);
                    if (users.getValue() != null && users.getValue().size() != 0) {
                        if (request.getPageIndex() != 0) request.setPageIndex(0);
                        clearAdapterEvent.call();
                    }
                    searchPublish.onNext(request.getSearchText());
                    break;
            }
        }
    };

}
