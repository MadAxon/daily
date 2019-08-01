package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import javax.inject.Inject;

import ru.vital.daily.enums.Direction;
import ru.vital.daily.enums.OrderBy;

@JsonObject
public class MessagesRequest extends ItemsRequest {

    @JsonField
    private Long chatId;

    @Inject
    public MessagesRequest() {
        setPageIndex(0);
        setDirection(Direction.desc.name());
        setOrderBy(OrderBy.id.name());
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
