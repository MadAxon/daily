package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import ru.vital.daily.repository.model.MessageInfoModel;

@JsonObject
@Entity(tableName = "messages", foreignKeys = @ForeignKey(
        entity = Chat.class,
        parentColumns = "id",
        childColumns = "chatId",
        onDelete = ForeignKey.CASCADE
))
public class Message {

    @PrimaryKey
    @JsonField
    private long id;

    @JsonField
    private long chatId;

    @JsonField
    @Nullable
    private String text, type, contentType;

    @JsonField
    @Nullable
    private Date createdAt, updatedAt;

    @JsonField
    @Nullable
    private List<Media> medias;

    @JsonField
    @Nullable
    private MessageInfoModel info;

    @JsonField
    @Nullable
    private User author;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getContentType() {
        return contentType;
    }

    public void setContentType(@Nullable String contentType) {
        this.contentType = contentType;
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nullable
    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(@Nullable List<Media> medias) {
        this.medias = medias;
    }

    @Nullable
    public MessageInfoModel getInfo() {
        return info;
    }

    public void setInfo(@Nullable MessageInfoModel info) {
        this.info = info;
    }

    @Nullable
    public User getAuthor() {
        return author;
    }

    public void setAuthor(@Nullable User author) {
        this.author = author;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
