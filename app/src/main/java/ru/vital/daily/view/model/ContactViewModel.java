package ru.vital.daily.view.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.databinding.Observable;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import ru.vital.daily.BR;
import ru.vital.daily.enums.Direction;
import ru.vital.daily.enums.OrderBy;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.request.AddMembersRequest;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.util.DisposableProvider;

public class ContactViewModel extends ViewModel {

    public final String groupUsersFragmentTag = "groupUsersFragmentTag";

    public MutableLiveData<List<User>> users = new MutableLiveData<>();

    public SingleLiveEvent<Void> clearAdapterEvent = new SingleLiveEvent<>(),
                            sendClickEvent = new SingleLiveEvent<>();

    public final ItemsRequest itemsRequest;

    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private final KeyRepository keyRepository;

    private final AddMembersRequest request;

    private long myUserId;

    private final PublishSubject<String> searchPublish = PublishSubject.create();

    @Inject
    public ContactViewModel(UserRepository userRepository, AddMembersRequest request, ChatRepository chatRepository, ItemsRequest itemsRequest, KeyRepository keyRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.keyRepository = keyRepository;
        this.request = request;

        itemsRequest.setPageSize(-1);
        itemsRequest.setPageIndex(0);
        itemsRequest.setOrderBy(OrderBy.onlineAt.name());
        itemsRequest.setDirection(Direction.desc.name());
        this.itemsRequest = itemsRequest;
        this.itemsRequest.addOnPropertyChangedCallback(onPropertyChangedCallback);

        compositeDisposable.add(searchPublish
                .debounce(1, TimeUnit.SECONDS)
                .switchMap(string -> {
                    isLoading.set(true);
                    if (string.isEmpty())
                        return userRepository.getUsers(myUserId, itemsRequest).toObservable();
                    else return userRepository.getUsersFromApi(itemsRequest).toObservable();
                })
                .subscribe(new ItemsResponseHandler<User>(userList -> {
                    users.postValue(userList);
                }, throwable -> {
                    errorEvent.postValue(throwable);
                    isLoading.set(false);
                }) {
                    @Override
                    public void accept(ItemsResponse<User> itemResponse) throws Exception {
                        super.accept(itemResponse);
                        if (itemResponse.getStatusCode() != 0)
                            isLoading.set(false);
                    }
                }, throwable -> {
                    errorEvent.postValue(throwable);
                    isLoading.set(false);
                }));
        DisposableProvider.getDisposableItem(keyRepository.getCurrentKey(),
                key -> {
                    myUserId = key.getUserId();
                    searchPublish.onNext("");
                }, throwable -> {
                    searchPublish.onNext("");
                });
    }

    public void addMembersToChat(Consumer<Void> onContinue, long[] ids) {
        isLoading.set(true);
        request.setMemberIds(ids);
        compositeDisposable.add(chatRepository.addMembersToChat(request,
                onContinue,
                throwable -> {
                    isLoading.set(false);
                    errorEvent.setValue(throwable);
                }));
    }

    public void onSendClicked() {
        sendClickEvent.call();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        itemsRequest.removeOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    private Observable.OnPropertyChangedCallback onPropertyChangedCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
                case BR.searchText:
                    if (!isLoading.get()) isLoading.set(true);
                    if (users.getValue() != null && users.getValue().size() != 0) users.postValue(new ArrayList<>());
                    searchPublish.onNext(itemsRequest.getSearchText());
                    break;
            }
        }
    };

    public AddMembersRequest getRequest() {
        return request;
    }
}
