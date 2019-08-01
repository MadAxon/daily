package ru.vital.daily.view.model.item;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.User;

public class MessageContactDailyItemViewModel extends MessageItemViewModel {

    private SingleLiveEvent<User> userClickedEvent;

    public void onUserClicked() {
        userClickedEvent.setValue(item.getAccount());
    }

    public void setUserClickedEvent(SingleLiveEvent<User> userClickedEvent) {
        this.userClickedEvent = userClickedEvent;
    }
}
