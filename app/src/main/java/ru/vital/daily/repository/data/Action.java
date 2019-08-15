package ru.vital.daily.repository.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "actions")
public class Action {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long messageId, chatId;

    private String action, messageIds;

    /**
     * For deleting messages only
     */
    private boolean forAll;

    /**
     * For forwarding messages only
     */
    private long fromChatId;

    public Action() {
    }

    @Ignore
    public Action(long messageId, long chatId, String action) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.action = action;
    }

    @Ignore
    public Action(long messageId, long chatId, String action, boolean forAll) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.action = action;
        this.forAll = forAll;
    }

    @Ignore
    public Action(String messageIds, long chatId, String action, boolean forAll) {
        this.messageIds = messageIds;
        this.chatId = chatId;
        this.action = action;
        this.forAll = forAll;
    }

    @Ignore
    public Action(String messageIds, long chatId, long fromChatId, String action) {
        this.messageIds = messageIds;
        this.chatId = chatId;
        this.fromChatId = fromChatId;
        this.action = action;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(String messageIds) {
        this.messageIds = messageIds;
    }

    public boolean getForAll() {
        return forAll;
    }

    public void setForAll(boolean forAll) {
        this.forAll = forAll;
    }

    public long getFromChatId() {
        return fromChatId;
    }

    public void setFromChatId(long fromChatId) {
        this.fromChatId = fromChatId;
    }
}
