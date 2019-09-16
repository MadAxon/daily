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

    @JsonField
    private long[] ids;

    @Inject
    public MessagesRequest() {
        setPageIndex(0);
        setDirection(Direction.desc.name());
        setOrderBy(OrderBy.id.name());
    }

    public MessagesRequest(Long chatId) {
        setPageIndex(0);
        setDirection(Direction.desc.name());
        setOrderBy(OrderBy.id.name());
        this.chatId = chatId;
    }

    public MessagesRequest(long[] ids, Long chatId) {
        setPageIndex(0);
        setPageSize(-1);
        setDirection(Direction.desc.name());
        setOrderBy(OrderBy.id.name());
        this.chatId = chatId;
        this.ids = ids;
    }

    public MessagesRequest(Long chatId, int pageSize) {
        setPageIndex(0);
        setPageSize(pageSize);
        setDirection(Direction.desc.name());
        setOrderBy(OrderBy.id.name());
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public long[] getIds() {
        return ids;
    }

    public void setIds(long[] ids) {
        this.ids = ids;
    }
}
