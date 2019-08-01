package ru.vital.daily.repository.api.response.handler;

import java.util.List;

import io.reactivex.functions.Consumer;
import ru.vital.daily.repository.api.response.ErrorResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;

public class ItemsResponseHandler<M> implements Consumer<ItemsResponse<M>> {

    private final Consumer<List<M>> successConsumer;

    private final Consumer<Throwable> errorConsumer;

    public ItemsResponseHandler() {
        successConsumer = null;
        errorConsumer = null;
    }

    public ItemsResponseHandler(Consumer<List<M>> successConsumer, Consumer<Throwable> errorConsumer) {
        this.successConsumer = successConsumer;
        this.errorConsumer = errorConsumer;
    }

    @Override
    public void accept(ItemsResponse<M> itemResponse) throws Exception {
        switch (itemResponse.getStatusCode()) {
            case 0:
            case 200:
                successConsumer.accept(itemResponse.getItems());
                break;
            case 401:
                errorConsumer.accept(new ErrorResponse(itemResponse.getMessage(), itemResponse.getStatusCode()));
                break;
            default:
                errorConsumer.accept(new Throwable(itemResponse.getMessage()));
        }
    }

}
