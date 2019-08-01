package ru.vital.daily.view.model.item;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;

public class MediaItemViewModel extends ItemViewModel<Media> {

    private SingleLiveEvent<Long> mediaClickEvent;

    private SingleLiveEvent<Media> checkClickEvent;

    @Override
    public void onItemClicked() {
        mediaClickEvent.setValue(item.getId());
    }

    public void onCheckClickedEvent(boolean isChecked) {
        if (isChecked != item.getSelected())
            checkClickEvent.setValue(item);
    }

    public void setCheckClickEvent(SingleLiveEvent<Media> checkClickEvent) {
        this.checkClickEvent = checkClickEvent;
    }

    public void setMediaClickEvent(SingleLiveEvent<Long> mediaClickEvent) {
        this.mediaClickEvent = mediaClickEvent;
    }
}
