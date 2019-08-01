package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import javax.inject.Inject;

@JsonObject
public class ItemRequest<M> {

    @JsonField
    private M item;

    @Inject
    public ItemRequest() {
    }

    public ItemRequest(M item) {
        this.item = item;
    }

    public M getItem() {
        return item;
    }

    public void setItem(M item) {
        this.item = item;
    }
}
