package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class ItemRequest<M> {

    @JsonField
    private M item;

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
