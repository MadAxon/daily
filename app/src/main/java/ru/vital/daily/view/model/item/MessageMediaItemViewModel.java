package ru.vital.daily.view.model.item;

import ru.vital.daily.enums.FileType;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.util.FileSize;
import ru.vital.daily.util.FileUtil;

public class MessageMediaItemViewModel extends MessageItemViewModel {

    private MessageMediaClickListener mediaClickListener;

    @Override
    public void setItem(Message item) {
        super.setItem(item);
        int size = item.getMedias().size();
        for (int i = 0; i < size; i++) {
            Media media = item.getMedias().valueAt(i);
            if (FileType.video.name().equals(media.getType()) && !FileUtil.exists(media.getFiles().get(0).getUrl()) && media.getFiles().get(0).getSize() <= FileSize.MB_LIMIT_VIDEO_FOR_AUTO_UPLOAD)
                mediaClickListener.startDownload(item, media);
        }
    }

    public void setMediaClickListener(MessageMediaClickListener mediaClickListener) {
        this.mediaClickListener = mediaClickListener;
    }

    public MessageMediaClickListener getMediaClickListener() {
        return mediaClickListener;
    }



}
