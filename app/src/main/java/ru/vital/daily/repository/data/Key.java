package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "keys")
@JsonObject
public class Key {

    @PrimaryKey
    @NonNull
    @JsonField
    private String accessKey = "";

    @JsonField
    private String renewKey, notificationTopic;

    @JsonField
    private Date expiresAt;

    @Ignore
    @JsonField
    private boolean isNewProfile;

    @JsonIgnore
    private boolean isCurrent = true;

    @JsonIgnore
    private long userId;

    @NonNull
    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(@NonNull String accessKey) {
        this.accessKey = accessKey;
    }

    public String getRenewKey() {
        return renewKey;
    }

    public void setRenewKey(String renewKey) {
        this.renewKey = renewKey;
    }

    public String getNotificationTopic() {
        return notificationTopic;
    }

    public void setNotificationTopic(String notificationTopic) {
        this.notificationTopic = notificationTopic;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean getIsNewProfile() {
        return isNewProfile;
    }

    public void setIsNewProfile(boolean newProfile) {
        isNewProfile = newProfile;
    }

    public boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(boolean current) {
        isCurrent = current;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
