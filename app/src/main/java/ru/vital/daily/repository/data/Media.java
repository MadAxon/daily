package ru.vital.daily.repository.data;

import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import ru.vital.daily.BR;
import ru.vital.daily.repository.model.MediaModel;

@JsonObject
public class Media extends BaseObservable {

    @JsonField
    private long id;

    @JsonField
    @Nullable
    private String name, type, moderatingStatus, description;

    @JsonField
    @Nullable
    private List<MediaModel> files;

    @JsonField
    @Nullable
    private Date createdAt, updatedAt;

    @JsonIgnore
    private boolean hasIconForProgress, selected, forceCancelled;

    @JsonIgnore
    private Float progress;

    @JsonIgnore
    private int current;

    /**
     * //playing
     * null - stopped or not started yet
     * false - paused
     * true - run
     */
    @JsonIgnore
    private Boolean playing;

    public Media() {
    }

    public Media(long id) {
        this.id = id;
    }

    public Media(@Nullable String name, @Nullable String type, @Nullable MediaModel file) {
        this.name = name;
        this.type = type;
        files = new ArrayList<>(1);
        files.add(file);
    }

    public Media(long id, @Nullable String type, @Nullable MediaModel file) {
        this.id = id;
        this.type = type;
        files = new ArrayList<>(1);
        files.add(file);
    }

    public Media(long id, String name, @Nullable String type, @Nullable MediaModel file) {
        this.id = id;
        this.type = type;
        this.progress = 0f;
        this.name = name;
        files = new ArrayList<>(1);
        files.add(file);
    }

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
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
        Collections.sort(files, Collections.reverseOrder());
    }

    @Bindable
    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
        Log.i("my_logs", "progress " + progress);
        notifyPropertyChanged(BR.progress);
    }

    @Bindable
    public boolean getHasIconForProgress() {
        return hasIconForProgress;
    }

    public void setHasIconForProgress(boolean hasIconForProgress) {
        this.hasIconForProgress = hasIconForProgress;
        notifyPropertyChanged(BR.hasIconForProgress);
    }

    @Bindable
    public Boolean getPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
        notifyPropertyChanged(BR.playing);
    }

    @Bindable
    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
        notifyPropertyChanged(BR.current);
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

    @Bindable
    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
        Log.i("my_logs", "id " + id + ", selected " + selected);
    }

    @Nullable
    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    public boolean getForceCancelled() {
        return forceCancelled;
    }

    public void setForceCancelled(boolean forceCancelled) {
        this.forceCancelled = forceCancelled;
    }
}
