package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import androidx.annotation.Nullable;

@JsonObject
public class MessageInfoModel {

    @JsonField
    @Nullable
    private Boolean hidden;

    @JsonField
    @Nullable
    private Date readAt;

    @Nullable
    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(@Nullable Boolean hidden) {
        this.hidden = hidden;
    }

    @Nullable
    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(@Nullable Date readAt) {
        this.readAt = readAt;
    }
}
