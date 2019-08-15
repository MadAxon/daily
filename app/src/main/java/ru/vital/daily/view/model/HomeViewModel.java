package ru.vital.daily.view.model;

import android.util.Log;

import androidx.databinding.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import ru.vital.daily.BR;
import ru.vital.daily.enums.Direction;
import ru.vital.daily.enums.OrderBy;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.ChatRepository;
import ru.vital.daily.repository.DraftRepository;
import ru.vital.daily.repository.MessageRepository;
import ru.vital.daily.repository.api.request.IdRequest;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.api.response.handler.ItemResponseHandler;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Draft;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.util.DisposableProvider;

public class HomeViewModel extends ViewModel {

    public final SingleLiveEvent<List<Chat>> searchChats = new SingleLiveEvent<>();

    private final ChatRepository chatRepository;

    private final DraftRepository draftRepository;

    private final ItemsRequest itemsRequest;

    private final List<Chat> chats = new ArrayList<>();

    private final List<Draft> drafts = new ArrayList<>();

    private Disposable chatDisposable;

    private Draft draft;

    private final PublishSubject<String> searchPublish = PublishSubject.create();

    private String toolbarTitle;

    @Inject
    public HomeViewModel(ChatRepository chatRepository, DraftRepository draftRepository, ItemsRequest itemsRequest) {
        this.chatRepository = chatRepository;
        this.draftRepository = draftRepository;
        this.itemsRequest = itemsRequest;

        this.itemsRequest.setPageIndex(0);
        this.itemsRequest.setPageSize(-1);
        this.itemsRequest.setDirection(Direction.desc.name());
        this.itemsRequest.setOrderBy(OrderBy.messagingAt.name());

        compositeDisposable.add(searchPublish
                .debounce(1, TimeUnit.SECONDS)
                .switchMap(string -> {
                    isLoading.set(true);
                    itemsRequest.setSearchText(string);
                    return chatRepository.getChats(itemsRequest).toObservable();
                })
                .subscribe(new ItemsResponseHandler<>(chatList -> {
                    chats.clear();
                    chats.addAll(chatList);
                    searchChats.postValue(chats);
                    isLoading.set(false);
                }, throwable -> {
                    errorEvent.postValue(throwable);
                    isLoading.set(false);
                }), throwable -> {
                    errorEvent.postValue(throwable);
                    isLoading.set(false);
                }));
    }

    public void getChats(Consumer<List<Chat>> fromDB, Consumer<List<Chat>> fromApi) {
        if (chatDisposable != null && !chatDisposable.isDisposed()) return;
        //compositeDisposable.clear();
        isLoading.set(true);
        compositeDisposable.add(chatDisposable = chatRepository.getChats(itemsRequest, this.chats.size() == 0).doOnComplete(() -> isLoading.set(false)).subscribe(new ItemsResponseHandler<Chat>(chats -> {
        }, throwable -> {
            errorEvent.setValue(throwable);
            isLoading.set(false);
        }) {
            @Override
            public void accept(ItemsResponse<Chat> itemResponse) throws Exception {
                super.accept(itemResponse);
                switch (itemResponse.getStatusCode()) {
                    case 200:
                        chats.clear();
                        chats.addAll(itemResponse.getItems());
                        fromApi.accept(chats);
                        break;
                    case 0:
                        chats.clear();
                        chats.addAll(itemResponse.getItems());
                        fromDB.accept(chats);
                        break;
                }
            }
        }, throwable -> {
            errorEvent.setValue(throwable);
            isLoading.set(false);
        }));
    }

    public void setDraft(Draft draft) {
        this.draft = draft;
    }

    public void saveChats(List<Chat> chat) {
        chatRepository.saveChats(chat);
    }

    public void saveChat(Chat chat) {
        chatRepository.saveChat(chat);
    }

    public void updateChat(Chat chat) {
        chatRepository.updateChat(chat);
    }

    public void getChat(Consumer<Chat> chatConsumer, long id) {
        DisposableProvider.getDisposableItem(chatRepository.getChat(new IdRequest(id)), chat -> {
            chatConsumer.accept(chat);
        }, throwable -> {

        });
    }

    public void getDrafts(Consumer<List<Draft>> consumer) {
        compositeDisposable.add(DisposableProvider.getDisposableItems(draftRepository.get(), drafts -> {
            this.drafts.clear();
            this.drafts.addAll(drafts);
            consumer.accept(drafts);
        }, throwable -> {
            consumer.accept(new ArrayList<>());
        }));
    }

    public List<Draft> getDrafts() {
        return drafts;
    }

    public Draft getDraft() {
        return draft;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setSearch(String searchText) {
        searchPublish.onNext(searchText);
    }

    public String getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(String toolbarTitle) {
        this.toolbarTitle = toolbarTitle;
    }

}
