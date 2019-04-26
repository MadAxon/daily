package ru.vital.daily.di.component;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.vital.daily.DailyApplication;
import ru.vital.daily.di.builder.ActivityBuilderModule;
import ru.vital.daily.di.module.AppModule;
import ru.vital.daily.di.module.RoomModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        RoomModule.class,
        ActivityBuilderModule.class,
})
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();

    }

    void inject(DailyApplication dailyApplication);

}
