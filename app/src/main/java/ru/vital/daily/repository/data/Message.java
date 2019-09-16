package ru.vital.daily.repository.data;

import android.widget.LinearLayout;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import ru.vital.daily.BR;
import ru.vital.daily.repository.api.converter.MediaArrayConverter;
import ru.vital.daily.repository.model.MessageContentModel;
import ru.vital.daily.repository.model.MessageInfoModel;

@JsonObject
@Entity(tableName = "messages",
        foreignKeys = @ForeignKey(
                entity = Chat.class,
                parentColumns = "id",
                childColumns = "chatId",
                onDelete = ForeignKey.CASCADE
        ), indices = {
                @Index(value = {"id", "chatId", "shouldSync"}, unique = true)
            }
        )
public class Message extends BaseObservable {

    @JsonField
    @PrimaryKey(autoGenerate = true)
    private long id;

    @JsonField
    private long chatId;

    @JsonIgnore
    private boolean shouldSync = false;

    @JsonField
    private Long forwardId;
    @JsonField
    private Long forwardAuthorId;

    @JsonField
    private Long accountId;

    @JsonField
    @Nullable
    private String text, type, contentType;

    @JsonField
    @Nullable
    private Date createdAt, updatedAt, readAt;

    @JsonIgnore
    private long checkedAt;

    @JsonField(typeConverter = MediaArrayConverter.class)
    @Nullable
    private LongSparseArray<Media> medias;

    @JsonField
    @Nullable
    private MessageInfoModel info;

    @JsonField
    @Nullable
    private User author, account, forwardAuthor;

    @JsonIgnore
    @Ignore
    private Date headerDate;

    @JsonIgnore
    @Ignore
    private boolean selected;

    @JsonIgnore
    @Nullable
    private Boolean sendStatus = true;

    @JsonField
    private MessageContentModel content;

    @JsonField
    private Message reply;

    @JsonIgnore
    private int gridHeight;

    @JsonIgnore
    @Ignore
    private boolean replyAnimationEvent;

    @JsonIgnore
    @Ignore
    private LinearLayout linearLayout;

    public Message() {
    }

    public Message(long id) {
        this.id = id;
    }

    public Message(Date headerDate) {
        this.headerDate = headerDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    @Bindable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
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
    @Bindable
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable Date updatedAt) {
        this.updatedAt = updatedAt;
        notifyPropertyChanged(BR.updatedAt);
    }

    @Nullable
    public LongSparseArray<Media> getMedias() {
        return medias;
    }

    public void setMedias(@Nullable LongSparseArray<Media> medias) {
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

    public Date getHeaderDate() {
        return headerDate;
    }

    public void setHeaderDate(Date headerDate) {
        this.headerDate = headerDate;
    }

    public long getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(long checkedAt) {
        this.checkedAt = checkedAt;
    }

    @Bindable
    public boolean getSelected() {
        return selected;
    }

    @Bindable
    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Nullable
    @Bindable
    public Boolean getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(@Nullable Boolean sendStatus) {
        this.sendStatus = sendStatus;
        notifyPropertyChanged(BR.sendStatus);
    }

    public boolean getShouldSync() {
        return shouldSync;
    }

    public void setShouldSync(boolean shouldSync) {
        this.shouldSync = shouldSync;
    }

    public MessageContentModel getContent() {
        return content;
    }

    public void setContent(MessageContentModel content) {
        this.content = content;
    }

    @Nullable
    public User getAccount() {
        return account;
    }

    public void setAccount(@Nullable User account) {
        this.account = account;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    @Bindable
    public boolean getReplyAnimationEvent() {
        return replyAnimationEvent;
    }

    public void setReplyAnimationEvent(boolean replyAnimationEvent) {
        this.replyAnimationEvent = replyAnimationEvent;
        notifyPropertyChanged(BR.replyAnimationEvent);
    }

    public Long getForwardId() {
        return forwardId;
    }

    public void setForwardId(Long forwardId) {
        this.forwardId = forwardId;
    }

    public Long getForwardAuthorId() {
        return forwardAuthorId;
    }

    public void setForwardAuthorId(Long forwardAuthorId) {
        this.forwardAuthorId = forwardAuthorId;
    }

    @Nullable
    public User getForwardAuthor() {
        return forwardAuthor;
    }

    public void setForwardAuthor(@Nullable User forwardAuthor) {
        this.forwardAuthor = forwardAuthor;
    }

    @Nullable
    @Bindable
    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(@Nullable Date readAt) {
        this.readAt = readAt;
        notifyPropertyChanged(BR.readAt);
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }


}
