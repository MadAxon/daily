package ru.vital.daily.repository.api.response;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

@JsonObject
public class ItemsResponse<M> extends BaseResponse<M> {

    @JsonField
    private List<M> items;

    public ItemsResponse() {
    }

    public ItemsResponse(List<M> items) {
        this.items = items;
    }

    public List<M> getItems() {
        return items;
    }

    public void setItems(List<M> items) {
        this.items = items;
    }
}
