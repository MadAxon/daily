package ru.vital.daily.view.model.item;

import ru.vital.daily.listener.SingleLiveEvent;

public class ChatItemViewModel extends ItemViewModel<Object> {

    public SingleLiveEvent<Object> itemLongClickedEvent;

    public boolean onItemLongClicked() {
        itemLongClickedEvent.setValue(item);
        return true;
    }

}
