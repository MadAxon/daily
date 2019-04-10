package ru.vital.daily.view.model.item;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;

public class UserStoryItemViewModel extends ItemViewModel<Object> {

    @Bindable
    public Boolean isMyStory = null;

    @Override
    public void setItem(Object item) {

    }

}
