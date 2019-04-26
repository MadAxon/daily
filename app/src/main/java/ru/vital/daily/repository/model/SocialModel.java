package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import androidx.annotation.Nullable;

@JsonObject
public class SocialModel {

    @JsonField
    @Nullable
    private String vk, fb;

    @Nullable
    public String getVk() {
        return vk;
    }

    public void setVk(@Nullable String vk) {
        this.vk = vk;
    }

    @Nullable
    public String getFb() {
        return fb;
    }

    public void setFb(@Nullable String fb) {
        this.fb = fb;
    }
}
