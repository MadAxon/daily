package ru.vital.daily.repository.api.response;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import androidx.annotation.Nullable;

@JsonObject
public class SocketResponse {

    @JsonField
    @Nullable
    private Long locationId, chatId, accountId;

    @JsonField
    @Nullable
    private long[] chatMessageIds;

    @Nullable
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(@Nullable Long accountId) {
        this.accountId = accountId;
    }

    @Nullable
    public long[] getChatMessageIds() {
        return chatMessageIds;
    }

    public void setChatMessageIds(@Nullable long[] chatMessageIds) {
        this.chatMessageIds = chatMessageIds;
    }

    @Nullable
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(@Nullable Long locationId) {
        this.locationId = locationId;
    }

    @Nullable
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(@Nullable Long chatId) {
        this.chatId = chatId;
    }

}
