package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ru.vital.daily.repository.model.ChatInfoModel;

@Entity(tableName = "chats")
@JsonObject
public class Chat {

    @JsonField
    @PrimaryKey
    private long id;

    @JsonField
    @Nullable
    private String name, type;

    @JsonField
    @Nullable
    private ChatInfoModel info;

    @JsonField
    @Nullable
    private List<User> members;

    @JsonIgnore
    private long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
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
}
