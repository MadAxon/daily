package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import ru.vital.daily.BR;

@JsonObject
public class Metadata extends BaseObservable {

    @JsonField
    private int duration;

    @Bindable
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }
}
