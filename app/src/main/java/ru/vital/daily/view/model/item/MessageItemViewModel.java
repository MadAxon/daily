package ru.vital.daily.view.model.item;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.api.ProgressApi;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;

public class MessageItemViewModel extends ItemViewModel<Message> {

    private User anotherUser;

    private SingleLiveEvent<Message> itemLongClickedEvent;

    private SingleLiveEvent<Message> replyClicked;

    private Media mediaReply;

    private Integer replyStringId;

    @Override
    public void setItem(Message item) {
        super.setItem(item);
        if (item != null && item.getReply() != null && item.getReply().getMedias() != null && !item.getReply().getMedias().isEmpty()) {
            mediaReply = item.getReply().getMedias().valueAt(0);
            if (FileType.image.name().equals(mediaReply.getType()))
                replyStringId = R.string.chat_message_photo;
            else if (FileType.video.name().equals(mediaReply.getType()))
                replyStringId = R.string.chat_message_video;
            else if (FileType.voice.name().equals(mediaReply.getType()))
                replyStringId = R.string.chat_message_voice;
            else replyStringId = R.string.chat_message_file;
        } else {
            mediaReply = null;
            replyStringId = null;
        }
    }

    public boolean onItemLongClicked() {
        itemLongClickedEvent.setValue(item);
        return true;
    }

    public void onReplyClicked() {
        replyClicked.setValue(item.getReply());
    }

    public User getAnotherUser() {
        return anotherUser;
    }

    public void setAnotherUser(User anotherUser) {
        this.anotherUser = anotherUser;
    }

    public void setItemLongClickedEvent(SingleLiveEvent<Message> itemLongClickedEvent) {
        this.itemLongClickedEvent = itemLongClickedEvent;
    }

    public Media getMediaReply() {
        return mediaReply;
    }

    public Integer getReplyStringId() {
        return replyStringId;
    }

    public void setReplyClicked(SingleLiveEvent<Message> replyClicked) {
        this.replyClicked = replyClicked;
    }
}
