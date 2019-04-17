package ru.vital.daily.repository.api.response;

public class ItemResponse<M> extends BaseResponse {

    private M item;

    public M getItem() {
        return item;
    }

    public void setItem(M item) {
        this.item = item;
    }
}
