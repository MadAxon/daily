package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class MessageReadRequest extends IdsRequest {

    @JsonField
    private long chatId;

    public MessageReadRequest() {
    }

    public MessageReadRequest(long[] ids, long chatId) {
        super(ids);
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
