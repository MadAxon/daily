package ru.vital.daily.view.model.item;

import android.util.Log;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import java.util.concurrent.atomic.AtomicBoolean;

import ru.vital.daily.BR;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.util.FileSize;
import ru.vital.daily.util.FileUtil;

public class MessageMediaItemViewModel extends MessageItemViewModel {

    private MessageMediaClickListener mediaClickListener;

    public final ObservableField<AtomicBoolean> forceUpdate = new ObservableField<>(new AtomicBoolean(false));

    @Override
    public void setItem(Message item) {
        super.setItem(item);
        int size = item.getMedias().size();
        for (int i = 0; i < size; i++) {
            Media media = item.getMedias().valueAt(i);
            if (!forceUpdate.get().get() && (media.getDescription() != null && !media.getDescription().isEmpty())) {
                forceUpdate.get().set(true);
            }
            media.addOnPropertyChangedCallback(onPropertyChangedCallback);
            if (FileType.video.name().equals(media.getType()) && !FileUtil.exists(media.getFiles().get(0).getUrl()) && media.getFiles().get(0).getSize() <= FileSize.MB_LIMIT_VIDEO_FOR_AUTO_UPLOAD && !media.getForceCancelled())
                mediaClickListener.startDownload(item, media);
            if (item.getSendStatus() == null && media.getId() <= 0) {
                mediaClickListener.continueUploading(media);
            }
        }
    }

    public void setMediaClickListener(MessageMediaClickListener mediaClickListener) {
        this.mediaClickListener = mediaClickListener;
    }

    public MessageMediaClickListener getMediaClickListener() {
        return mediaClickListener;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        item.removeOnPropertyChangedCallback(onPropertyChangedCallback);
    }

    private final OnPropertyChangedCallback onPropertyChangedCallback = new OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
                case BR.description:
                    if (!forceUpdate.get().get())
                        forceUpdate.set(new AtomicBoolean(true));
                    break;
            }
        }
    };

}
