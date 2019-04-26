package ru.vital.daily.di.module;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.api.AccessInterceptor;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.api.response.handler.ItemsResponseHandler;
import ru.vital.daily.repository.data.Key;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    Api provideRetrofit(AccessInterceptor accessInterceptor) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .addInterceptor(accessInterceptor)
                        .build();
        return new Retrofit.Builder()
                .addConverterFactory(LoganSquareConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://dev.daily1.ru/api/")
                .client(okHttpClient)
                .build()
                .create(Api.class);
    }

    @Provides
    @Singleton
    AccessInterceptor provideInterceptor() {
        return new AccessInterceptor();
    }

    @Provides
    @Singleton
    InputMethodManager provideInputMethodManager(Application application) {
        return (InputMethodManager) application.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager(Application application) {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    IntentFilter provideIntentFilterConnectivity() {
        return new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Provides
    @Singleton
    PackageManager providePackageManager(Application application) {
        return application.getPackageManager();
    }

}
