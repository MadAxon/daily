package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class MessageRemoveRequest extends IdsRequest {

    @JsonField
    private long chatId;

    @JsonField
    private boolean forAll;

    public MessageRemoveRequest() {
    }

    public MessageRemoveRequest(long[] ids, long chatId, boolean forAll) {
        super(ids);
        this.chatId = chatId;
        this.forAll = forAll;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public boolean isForAll() {
        return forAll;
    }

    public void setForAll(boolean forAll) {
        this.forAll = forAll;
    }
}
