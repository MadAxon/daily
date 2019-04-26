package ru.vital.daily.util;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.repository.api.response.BaseResponse;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.api.response.handler.BaseResponseHandler;
import ru.vital.daily.repository.api.response.handler.ItemResponseHandler;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;

public final class DisposableProvider {

    public static <M> Disposable getDisposableItems(Single<ItemsResponse<M>> single, Consumer<List<M>> onSuccess, Consumer<Throwable> onError) {
        return single.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new ItemsResponseHandler<>(onSuccess, onError), onError);
    }

    public static <M> Disposable getDisposableItem(Single<ItemResponse<M>> single, Consumer<M> onSuccess, Consumer<Throwable> onError) {
        return single.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new ItemResponseHandler<>(onSuccess, onError), onError);
    }

    public static Disposable getDisposableBase(Single<BaseResponse> single, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        return single.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new BaseResponseHandler(onSuccess, onError), onError);
    }


    public static void doCallable(Callable callable) {
        Maybe.fromCallable(callable).toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe();
    }

}
