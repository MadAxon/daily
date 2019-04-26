package ru.vital.daily.repository.api.response.handler;

import io.reactivex.functions.Consumer;
import ru.vital.daily.repository.api.response.BaseResponse;
import ru.vital.daily.repository.api.response.ErrorResponse;

public class BaseResponseHandler implements Consumer<BaseResponse> {

    private Consumer<String> successConsumer;

    private Consumer<Throwable> errorConsumer;

    public BaseResponseHandler(Consumer<String> successConsumer, Consumer<Throwable> errorConsumer) {
        this.successConsumer = successConsumer;
        this.errorConsumer = errorConsumer;
    }

    @Override
    public void accept(BaseResponse baseResponse) throws Exception {
        switch (baseResponse.getStatusCode()) {
            case 200:
                successConsumer.accept(baseResponse.getMessage());
                break;
            case 401:
                errorConsumer.accept(new ErrorResponse(baseResponse.getMessage(), baseResponse.getStatusCode()));
                break;
            default:
                errorConsumer.accept(new Throwable(baseResponse.getMessage()));
        }
    }

}
