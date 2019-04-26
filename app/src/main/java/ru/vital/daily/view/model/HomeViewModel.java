package ru.vital.daily.view.model;

import javax.inject.Inject;

import ru.vital.daily.listener.SingleLiveEvent;

public class HomeViewModel extends ViewModel {

    public SingleLiveEvent<Void> toolbarClickedEvent = new SingleLiveEvent<>();

    @Inject
    public HomeViewModel() {

    }

    public void onToolbarClicked() {
        toolbarClickedEvent.call();
    }
}
