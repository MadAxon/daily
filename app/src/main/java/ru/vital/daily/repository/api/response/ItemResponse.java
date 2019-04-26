package ru.vital.daily.repository.api.response;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import androidx.annotation.Nullable;

@JsonObject
public class ItemResponse<M> extends BaseResponse<M> {

    @JsonField
    @Nullable
    private M item;

    public ItemResponse() {
    }

    public ItemResponse(@Nullable M item) {
        this.item = item;
    }

    @Nullable
    public M getItem() {
        return item;
    }

    public void setItem(@Nullable M item) {
        this.item = item;
    }
}
