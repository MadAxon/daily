package ru.vital.daily.repository.model;

import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import ru.vital.daily.BR;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import ru.vital.daily.enums.MessageType;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;

@JsonObject
public class MessageSendModel extends BaseObservable {

    @JsonField
    @Nullable
    // id "-1" - change media's description
    // id "-2" - reply variants
    private Long id, chatId, replyId, stickerId, accountId, postId, storyId, locationId;

    @JsonField
    @Nullable
    private String text, contentType;

    @JsonField
    @Nullable
    private List<Long> mediaIds;

    @JsonField
    private long[] forwardIds;

    @JsonIgnore
    @Nullable
    private LongSparseArray<Media> medias;

    @JsonIgnore
    private LongSparseArray<User> users;

    @JsonField
    @Nullable
    private MessageContentModel content;

    @JsonIgnore
    private String tabSubtitle, tabTitle;

    @JsonIgnore
    private Integer stringId;

    @JsonIgnore
    private Media audioFile, mediaForChanging;

    @JsonIgnore
    private Message reply;

    @Inject
    public MessageSendModel() {
    }

    public MessageSendModel(Message message) {
        setMessage(message);
    }

    @Nullable
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(@Nullable Long chatId) {
        this.chatId = chatId;
    }

    @Nullable
    @Bindable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Nullable
    @Bindable
    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(@Nullable Long replyId) {
        this.replyId = replyId;
        notifyPropertyChanged(BR.replyId);
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
    @Bindable
    public String getText() {
        return text;
    }

    public void setText(@Nullable String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
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
    public long[] getForwardIds() {
        return forwardIds;
    }

    public void setForwardIds(@Nullable long[] forwardIds) {
        this.forwardIds = forwardIds;
    }

    @Nullable
    public MessageContentModel getContent() {
        return content;
    }

    public void setContent(@Nullable MessageContentModel content) {
        this.content = content;
    }

    public void toDefault() {
        setId(null);
        setText(null);
        setContentType(null);
        setReplyId(null);
        setForwardIds(null);
        setLocationId(null);
        setStickerId(null);
        setPostId(null);
        setAccountId(null);
        setContent(null);
        setMedias(null);
        setAudioFile(null);
        setReply(null);
        //setStringId(null);
    }

    public void toDefaultNotIncludingText() {
        setId(null);
        setReply(null);
        setReplyId(null);
        setMediaForChanging(null);
        setStringId(null);
    }

    public Message getMessage() {
        Message message = new Message();
        if (id != null && id > 0) { // change message
            message.setId(id);
            message.setUpdatedAt(new Date());
        } else { // send a new message
            message.setShouldSync(true);
            message.setCreatedAt(new Date());
            message.setUpdatedAt(message.getCreatedAt());
            message.setType(MessageType.message.name());
            message.setContentType(contentType);
        }
        if (chatId != null) message.setChatId(chatId);
        if (users != null && !users.isEmpty()) {
            User user = users.valueAt(0);
            message.setAccount(user);
            users.remove(user.getId());
        }
        message.setText(text);
        if (medias != null)
            message.setMedias(medias.clone());
        message.setReply(reply);
        return message;
    }

    public void setMessage(Message message) {
        chatId = message.getChatId();
        setText(message.getText());
        contentType = message.getContentType();
        if (message.getReply() != null)
            replyId = message.getReply().getId();
        if (message.getAccount() != null)
            accountId = message.getAccount().getId();
        if (message.getMedias() != null && !message.getMedias().isEmpty()) {
            final int size = message.getMedias().size();
            mediaIds = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                long mediaId = message.getMedias().valueAt(i).getId();
                if (mediaId > 0) mediaIds.add(mediaId);
            }
        }
    }

    @Nullable
    @Bindable
    public LongSparseArray<Media> getMedias() {
        return medias;
    }

    public void setMedias(@Nullable LongSparseArray<Media> medias) {
        this.medias = medias;
        notifyPropertyChanged(BR.medias);
    }

    @Bindable
    public String getTabSubtitle() {
        return tabSubtitle;
    }

    public void setTabSubtitle(String tabSubtitle) {
        this.tabSubtitle = tabSubtitle;
        notifyPropertyChanged(BR.tabSubtitle);
    }

    @Bindable
    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
        notifyPropertyChanged(BR.tabTitle);
    }

    @Bindable
    public Media getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(Media audioFile) {
        this.audioFile = audioFile;
        notifyPropertyChanged(BR.audioFile);
    }

    public LongSparseArray<User> getUsers() {
        return users;
    }

    public void setUsers(LongSparseArray<User> users) {
        this.users = users;
    }

    @Bindable
    public Media getMediaForChanging() {
        return mediaForChanging;
    }

    public void setMediaForChanging(Media mediaForChanging) {
        this.mediaForChanging = mediaForChanging;
        notifyPropertyChanged(BR.mediaForChanging);
    }

    @Bindable
    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
        notifyPropertyChanged(BR.reply);
    }

    @Bindable
    public Integer getStringId() {
        return stringId;
    }

    public void setStringId(Integer stringId) {
        this.stringId = stringId;
        notifyPropertyChanged(BR.stringId);
    }
}
