package ru.vital.daily.view.binding;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.MessageMediaViewHolder;
import ru.vital.daily.databinding.ItemMessageMediaGridBinding;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;

public class LinearLayoutBinding {

    @BindingAdapter("android:layout_marginTop")
    public static void setMarginTop(LinearLayout linearLayout, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, Math.round(margin), layoutParams.rightMargin, layoutParams.bottomMargin);
    }


    @BindingAdapter(value = {"medias", "mediaClick", "mediaViewHolder"})
    public static void setMedias(LinearLayout linearLayout, final Message message, MessageMediaClickListener mediaClickListener, MessageMediaViewHolder mediaViewHolder) {
        Log.i("my_logs", "setMedias " + message.getId());
        final int size = message.getMedias().size();
        //linearLayout.removeAllViews();
        for (int i = 0; i < size; i++) {
            ItemMessageMediaGridBinding binding = DataBindingUtil.inflate(LayoutInflater.from(linearLayout.getContext()), R.layout.item_message_media_grid, null, false);

            Media media = message.getMedias().valueAt(i);
            if (FileType.image.name().equals(media.getType())) {
                binding.setImage(media);
                if (media.getDescription() != null && !media.getDescription().isEmpty()) {
                    //setDescription(gridLayout, binding, startItem, gridLayout.getResources().getDimension(R.dimen.message_media_bottom_padding_16dp));
                }
                binding.setVideo(null);
            } else {
                binding.setImage(null);
                binding.setVideo(media);
                if (media.getDescription() != null && !media.getDescription().isEmpty()) {
                    //setDescription(gridLayout, binding, startItem, gridLayout.getResources().getDimension(R.dimen.message_media_bottom_padding_16dp));
                }
                if (mediaViewHolder.getPlayerView() == null) {
                    mediaViewHolder.setPlayerView(binding.playerView);
                    mediaViewHolder.setRoundedImageView(binding.roundedImageView);
                    mediaViewHolder.setVideoContainer(binding.videoContainer);
                    mediaViewHolder.setMedia(media.getId());
                }
            }
            binding.setViewModel(mediaViewHolder.viewModel);
            binding.setMessage(message);
            binding.setMediaClickListener(mediaClickListener);
            binding.setCancelUploadListener((mediaMessage, media1) -> {
                if (media1.getId() <= 0) {
                    mediaClickListener.cancelUpload(mediaMessage, media1);
                    //gridLayout.removeViewAt(index);
                } else mediaClickListener.cancelDownload(mediaMessage, mediaMessage.getMedias().get(media1.getId()));
            });
            linearLayout.addView(binding.getRoot());
        }
    }

}
