package ru.vital.daily.repository.api.response.handler;

import io.reactivex.functions.Consumer;
import ru.vital.daily.repository.api.response.ErrorResponse;
import ru.vital.daily.repository.api.response.ItemResponse;

public class ItemResponseHandler<M> implements Consumer<ItemResponse<M>> {

    private final Consumer<M> successConsumer;

    private final Consumer<Throwable> errorConsumer;

    public ItemResponseHandler(Consumer<M> successConsumer, Consumer<Throwable> errorConsumer) {
        this.successConsumer = successConsumer;
        this.errorConsumer = errorConsumer;
    }

    @Override
    public void accept(ItemResponse<M> itemResponse) throws Exception {
        switch (itemResponse.getStatusCode()) {
            case 200:
                successConsumer.accept(itemResponse.getItem());
                break;
            case 401:
                errorConsumer.accept(new ErrorResponse(itemResponse.getMessage(), itemResponse.getStatusCode()));
                break;
            default:
                errorConsumer.accept(new Throwable(itemResponse.getMessage()));
        }
    }
}
