package ru.vital.daily.repository.model;

import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import ru.vital.daily.BR;
import ru.vital.daily.repository.data.Message;

@JsonObject
public class ChatInfoModel extends BaseObservable {

    @JsonField
    private Long lastMessageId;

    @JsonField
    private String role;

    @JsonField
    private Boolean hidden;

    @JsonField
    private Date mutedAt, unmuteAt, bannedAt, unbanAt, messagingAt;

    @JsonField
    private int unreadMessagesCount;

    @JsonField
    private Message lastMessage;

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Date getMutedAt() {
        return mutedAt;
    }

    public void setMutedAt(Date mutedAt) {
        this.mutedAt = mutedAt;
    }

    public Date getUnmuteAt() {
        return unmuteAt;
    }

    public void setUnmuteAt(Date unmuteAt) {
        this.unmuteAt = unmuteAt;
    }

    public Date getBannedAt() {
        return bannedAt;
    }

    public void setBannedAt(Date bannedAt) {
        this.bannedAt = bannedAt;
    }

    public Date getUnbanAt() {
        return unbanAt;
    }

    public void setUnbanAt(Date unbanAt) {
        this.unbanAt = unbanAt;
    }

    public Date getMessagingAt() {
        return messagingAt;
    }

    public void setMessagingAt(Date messagingAt) {
        this.messagingAt = messagingAt;
    }

    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    @Bindable
    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
        notifyPropertyChanged(BR.lastMessage);
    }
}
