package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ru.vital.daily.repository.model.MediaModel;

@Entity(tableName = "medias")
@JsonObject
public class Media {

    @PrimaryKey
    @JsonField
    private long id;

    @JsonField
    @Nullable
    private String name, type, moderatingStatus;

    @JsonField
    @Nullable
    private List<MediaModel> files;

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
    public String getModeratingStatus() {
        return moderatingStatus;
    }

    public void setModeratingStatus(@Nullable String moderatingStatus) {
        this.moderatingStatus = moderatingStatus;
    }

    @Nullable
    public List<MediaModel> getFiles() {
        return files;
    }

    public void setFiles(@Nullable List<MediaModel> files) {
        this.files = files;
    }
}
