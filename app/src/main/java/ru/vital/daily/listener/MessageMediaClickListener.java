package ru.vital.daily.listener;

import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;

public interface MessageMediaClickListener {

    void onImageOrVideoClick(Message message, Media media);

    void onAudioClick(Message message);

    void cancelDownload(Message message, Media media);

    void cancelUpload(Message message, Media media);

    void startDownload(Message message, Media media);

    void uploadMedia(Message message);

    void continueUploading(Media media);

    boolean onImageOrVideoLongClick(Message message, Media media);

    void heightChanged(Message message, int height);

}
