package ru.vital.daily.view.model.item;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import ru.vital.daily.BR;
import ru.vital.daily.repository.data.User;

public class ContactItemViewModel extends ItemViewModel<User> implements Observable {

    @Bindable
    private boolean selected;

    private final PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        callbacks.notifyChange(this, BR.selected);
    }
}
