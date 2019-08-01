package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "drafts")
@JsonObject
public class Draft {

    @PrimaryKey
    @JsonField
    private long id;

    @JsonField
    private Message message;

    public Draft() {
    }

    @Ignore
    public Draft(long id) {
        this.id = id;
    }

    @Ignore
    public Draft(long id, Message message) {
        this.id = id;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
