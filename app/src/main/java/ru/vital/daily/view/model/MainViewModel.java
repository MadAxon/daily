package ru.vital.daily.view.model;

import android.app.Application;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.data.Key;

public class MainViewModel extends ViewModel {

    public final String authFragmentTag = "authFragmentTag",
                    feedFragmentTag = "feedFragmentTag",
                    homeFragmentTag = "homeFragmentTag",
                    accountFragmentTag = "accountFragmentTag",
                    registerFragmentTag = "registerFragmentTag";

    public SingleLiveEvent<Void> mainShowEvent = new SingleLiveEvent<>();

    private final KeyRepository keyRepository;

    @Inject
    public MainViewModel(KeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    public Single<ItemResponse<Key>> getKeys() {
        return keyRepository.getCurrentKey();
    }
}
