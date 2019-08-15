package ru.vital.daily.di.module;

import android.app.Application;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import ru.vital.daily.repository.api.AccessInterceptor;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.ProgressApi;
import ru.vital.daily.repository.api.ProgressInterceptor;

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
                        /*.readTimeout(30, TimeUnit.MINUTES)
                        .writeTimeout(30, TimeUnit.MINUTES)
                        .connectTimeout(30, TimeUnit.MINUTES)*/
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
    AccessInterceptor provideAccessInterceptor() {
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

    @Provides
    @Singleton
    ClipboardManager provideClipboardManager(Application application) {
        return (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(Application application) {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

}
