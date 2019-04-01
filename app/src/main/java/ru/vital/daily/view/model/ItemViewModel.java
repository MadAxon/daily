package ru.vital.daily.view.model;

import androidx.lifecycle.ViewModel;

public abstract class ItemViewModel<M> extends ViewModel {

    public abstract void setItem(M item);

}
