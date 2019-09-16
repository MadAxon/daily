package ru.vital.daily.repository.model;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ru.vital.daily.BR;

@JsonObject
public class MediaModel extends BaseObservable implements Comparable<MediaModel> {

    @JsonField
    private String url, type;

    @JsonField
    private int size;

    @JsonField
    private Metadata metadata;

    public MediaModel() {
    }

    public MediaModel(String url, String type) {
        this.url = url;
        this.type = type;
    }

    public MediaModel(String url, String type, int size) {
        this.url = url;
        this.type = type;
        this.size = size;
    }

    @Bindable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        notifyPropertyChanged(BR.url);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Metadata getMetadata() {
        if (metadata == null) metadata = new Metadata();
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public int compareTo(MediaModel o) {
        return Integer.compare(size, o.size);
    }
}
