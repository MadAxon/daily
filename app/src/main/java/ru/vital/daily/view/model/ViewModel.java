package ru.vital.daily.view.model;

import android.app.Application;
import android.content.Intent;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.api.response.ErrorResponse;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;

public abstract class ViewModel extends androidx.lifecycle.ViewModel implements IViewModel {

    public final ObservableBoolean isLoading = new ObservableBoolean();

    public final SingleLiveEvent<Throwable> errorEvent = new SingleLiveEvent<>();

    protected final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
    }
}
