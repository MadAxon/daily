package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import ru.vital.daily.BR;
import ru.vital.daily.repository.model.ChatInfoModel;

@Entity(tableName = "chats")
@JsonObject
public class Chat extends BaseObservable {

    @JsonField
    @PrimaryKey
    private long id;

    @JsonField
    @Nullable
    private String uname, name, type;

    @JsonField
    @Nullable
    private ChatInfoModel info;

    @JsonField
    @Nullable
    private List<User> members;

    @JsonField
    @Nullable
    private Media cover;

    @JsonIgnore
    private Date messagingAt;

    @JsonIgnore
    private long userId;

    @JsonIgnore
    @Ignore
    private boolean typing, update;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getUname() {
        return uname;
    }

    public void setUname(@Nullable String uname) {
        this.uname = uname;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public ChatInfoModel getInfo() {
        return info;
    }

    public void setInfo(@Nullable ChatInfoModel info) {
        this.info = info;
        messagingAt = info.getMessagingAt();
    }

    @Nullable
    public List<User> getMembers() {
        return members;
    }

    public void setMembers(@Nullable List<User> members) {
        this.members = members;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public Date getMessagingAt() {
        return messagingAt;
    }

    public void setMessagingAt(Date messagingAt) {
        this.messagingAt = messagingAt;
    }

    @Nullable
    public Media getCover() {
        return cover;
    }

    public void setCover(@Nullable Media cover) {
        this.cover = cover;
    }

    @Bindable
    public boolean getTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
        notifyPropertyChanged(BR.typing);
    }

    @Bindable
    public boolean getUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
        notifyPropertyChanged(BR.update);
    }
}
