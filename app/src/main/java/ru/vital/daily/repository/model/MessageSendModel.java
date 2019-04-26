package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;

@JsonObject
public class MessageSendModel {

    @JsonField
    @Nullable
    private Long chatId, id, replyId, stickerId, accountId, postId, storyId, locationId;

    @JsonField
    @Nullable
    private String text, contentType;

    @JsonField
    @Nullable
    private List<Long> mediaIds, forwardIds;

    @JsonField
    @Nullable
    private MessageContentModel content;

    @Inject
    public MessageSendModel() {
    }

    @Nullable
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(@Nullable Long chatId) {
        this.chatId = chatId;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(@Nullable Long replyId) {
        this.replyId = replyId;
    }

    @Nullable
    public Long getStickerId() {
        return stickerId;
    }

    public void setStickerId(@Nullable Long stickerId) {
        this.stickerId = stickerId;
    }

    @Nullable
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(@Nullable Long accountId) {
        this.accountId = accountId;
    }

    @Nullable
    public Long getPostId() {
        return postId;
    }

    public void setPostId(@Nullable Long postId) {
        this.postId = postId;
    }

    @Nullable
    public Long getStoryId() {
        return storyId;
    }

    public void setStoryId(@Nullable Long storyId) {
        this.storyId = storyId;
    }

    @Nullable
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(@Nullable Long locationId) {
        this.locationId = locationId;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nullable
    public String getContentType() {
        return contentType;
    }

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Nullable
    public List<Long> getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(@Nullable List<Long> mediaIds) {
        this.mediaIds = mediaIds;
    }

    @Nullable
    public List<Long> getForwardIds() {
        return forwardIds;
    }

    public void setForwardIds(@Nullable List<Long> forwardIds) {
        this.forwardIds = forwardIds;
    }

    @Nullable
    public MessageContentModel getContent() {
        return content;
    }

    public void setContent(@Nullable MessageContentModel content) {
        this.content = content;
    }
}
