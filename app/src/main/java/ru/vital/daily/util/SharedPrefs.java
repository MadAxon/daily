package ru.vital.daily.util;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharedPrefs {

    public final String DAILY_PREFERENCES = "DAILY_PREFERENCES",
            DRAFT_PREFERENCE = "DRAFT_PREFERENCE",
            ONLINE_PREFERENCE = "ONLINE_PREFERENCE";

    private final Application application;

    @Inject
    public SharedPrefs(Application application) {
        this.application = application;
    }

    public void apply(String key, String value) {
        application.getSharedPreferences(DAILY_PREFERENCES, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public void applyOnline(boolean value) {
        application.getSharedPreferences(DAILY_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(ONLINE_PREFERENCE, value).apply();
    }

    public boolean getOnline() {
        return application.getSharedPreferences(DAILY_PREFERENCES, Context.MODE_PRIVATE).getBoolean(ONLINE_PREFERENCE, false);
    }

}
