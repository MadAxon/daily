package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import androidx.annotation.Nullable;

@JsonObject
public class RelationModel {

    @JsonField
    @Nullable
    private Boolean contact, followed;

    @JsonField
    @Nullable
    private Date mutedAt, unmuteAt, bannedAt, unbanAt;

    @Nullable
    public Boolean getContact() {
        return contact;
    }

    public void setContact(@Nullable Boolean contact) {
        this.contact = contact;
    }

    @Nullable
    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(@Nullable Boolean followed) {
        this.followed = followed;
    }

    @Nullable
    public Date getMutedAt() {
        return mutedAt;
    }

    public void setMutedAt(@Nullable Date mutedAt) {
        this.mutedAt = mutedAt;
    }

    @Nullable
    public Date getUnmuteAt() {
        return unmuteAt;
    }

    public void setUnmuteAt(@Nullable Date unmuteAt) {
        this.unmuteAt = unmuteAt;
    }

    @Nullable
    public Date getBannedAt() {
        return bannedAt;
    }

    public void setBannedAt(@Nullable Date bannedAt) {
        this.bannedAt = bannedAt;
    }

    @Nullable
    public Date getUnbanAt() {
        return unbanAt;
    }

    public void setUnbanAt(@Nullable Date unbanAt) {
        this.unbanAt = unbanAt;
    }
}
