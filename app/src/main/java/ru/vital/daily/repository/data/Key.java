package ru.vital.daily.repository.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "keys")
public class Key {

    @PrimaryKey
    @NonNull
    private String accessKey = "";

    @NonNull
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(@NonNull String accessKey) {
        this.accessKey = accessKey;
    }
}
