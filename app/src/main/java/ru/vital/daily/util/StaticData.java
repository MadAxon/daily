package ru.vital.daily.util;

import android.util.Log;

import javax.inject.Inject;

import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;

public final class StaticData {

    public final User profile;

    public final Key key;

    private static StaticData data;

    private StaticData(Key key, User profile) {
        this.key = key;
        this.profile = profile;
        Log.i("my_logs", "static data " + key.getUserId() + " | " + profile.getUname());
    }

    public static StaticData getData() {
        return data;
    }

    public static void setData(StaticData data) {
        StaticData.data = data;
    }

    public static void init(Key key, User profile) {
        if (data == null)
            data = new StaticData(key, profile);
    }

}
