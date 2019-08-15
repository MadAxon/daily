package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class MessageForwardRequest extends IdsRequest {

    @JsonField
    private long fromChatId, toChatId;

    public MessageForwardRequest() {
    }

    public MessageForwardRequest(long[] ids, long fromChatId, long toChatId) {
        super(ids);
        this.fromChatId = fromChatId;
        this.toChatId = toChatId;
    }

    public long getFromChatId() {
        return fromChatId;
    }

    public void setFromChatId(long fromChatId) {
        this.fromChatId = fromChatId;
    }

    public long getToChatId() {
        return toChatId;
    }

    public void setToChatId(long toChatId) {
        this.toChatId = toChatId;
    }
}
