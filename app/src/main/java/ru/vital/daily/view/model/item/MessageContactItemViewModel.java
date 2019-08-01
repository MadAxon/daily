package ru.vital.daily.view.model.item;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.User;

public class MessageContactItemViewModel extends MessageItemViewModel {

    private SingleLiveEvent<String> contactClickEvent;

    public void onContactClicked() {
        contactClickEvent.setValue(item.getContent().getPhone());
    }

    public void setContactClickEvent(SingleLiveEvent<String> contactClickEvent) {
        this.contactClickEvent = contactClickEvent;
    }
}
