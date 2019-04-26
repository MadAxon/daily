package ru.vital.daily.view.model.item;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Message;

public class MessageItemViewModel extends ItemViewModel<Message> {

    public SingleLiveEvent<Message> itemLongClickedEvent;

    public boolean onItemLongClicked() {
        itemLongClickedEvent.setValue(item);
        return true;
    }

}
