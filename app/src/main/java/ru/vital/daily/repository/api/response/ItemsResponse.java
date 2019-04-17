package ru.vital.daily.repository.api.response;

import java.util.List;

public class ItemsResponse<M> extends BaseResponse {

    private List<M> items;

    public List<M> getItems() {
        return items;
    }

    public void setItems(List<M> items) {
        this.items = items;
    }
}
