package ru.vital.daily.view.binding;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;

import ru.vital.daily.R;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.FileSize;
import ru.vital.daily.util.FileUtil;

public class ConstraintLayoutBinding {

    @BindingAdapter(value = {"chat_voice_item", "chat_voice_listener"})
    public static void setChatVoice(ConstraintLayout constraintLayout, Message message, MessageMediaClickListener mediaClickListener) {
        MediaModel mediaModel = message.getMedias().valueAt(0).getFiles().get(0);
        if (!FileUtil.exists(mediaModel.getUrl()) && mediaModel.getSize() <= FileSize.MB_LIMIT_IMAGE_FOR_AUTO_UPLOAD)
            mediaClickListener.startDownload(message, message.getMedias().valueAt(0));
    }

}
