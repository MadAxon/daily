package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class MessageRequest extends IdRequest {

    @JsonField
    private long chatId;

    public MessageRequest() {
    }

    public MessageRequest(Long id, long chatId) {
        super(id);
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
