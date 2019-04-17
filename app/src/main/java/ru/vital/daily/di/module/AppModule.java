package ru.vital.daily.di.module;

import android.app.Application;
import android.content.Context;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import ru.vital.daily.repository.api.Api;

@Module(includes = ViewModelModule.class)
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    Api provideRetrofit() {
        return new Retrofit.Builder()
                .addConverterFactory(LoganSquareConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://dev.daily1.ru/api/")
                .build()
                .create(Api.class);
    }

}
