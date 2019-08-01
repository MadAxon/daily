package ru.vital.daily.view.binding;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.util.List;

import androidx.collection.LongSparseArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import ru.vital.daily.R;
import ru.vital.daily.adapter.viewholder.MessageMediaViewHolder;
import ru.vital.daily.databinding.ItemMediaGridBinding;
import ru.vital.daily.databinding.ItemMessageMediaGridBinding;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.generated.callback.OnClickListener;
import ru.vital.daily.listener.MessageMediaClickListener;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.view.MediaGridLayout;

public class GridLayoutBinding {

    @BindingAdapter("android:layout_marginTop")
    public static void setMarginTop(GridLayout gridLayout, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) gridLayout.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, Math.round(margin), layoutParams.rightMargin, layoutParams.bottomMargin);
    }

    @BindingAdapter(value = {"medias", "mediaClick", "mediaViewHolder", "startItem"})
    public static void setMedias(final MediaGridLayout gridLayout, final Message message, MessageMediaClickListener mediaClickListener, MessageMediaViewHolder mediaViewHolder, boolean startItem) {
        if (gridLayout.getMessageId() != message.getId() || gridLayout.getShouldSync() != message.getShouldSync()) {
            Log.i("my_logs", "setMedias() " + message.getMedias().valueAt(0).getFiles().get(0).getUrl());
            gridLayout.setMessageId(message.getId());
            gridLayout.setShouldSync(message.getShouldSync());
            final LongSparseArray<Media> medias = message.getMedias();
            boolean hasDescription = false;
            int size;
            if (medias != null && (size = medias.size()) > 0) {
                for (int i = 0; i < size; i++) {
                    Media media = medias.valueAt(i);
                    if (media.getDescription() != null && !media.getDescription().isEmpty()) {
                        hasDescription = true;
                        break;
                    }
                }
                gridLayout.removeAllViews();
                //gridLayout.setRowCount((medias.size() / 2) + (medias.size() % 2));
                if (gridLayout.getWidth() == 0) {

                    Log.i("my_logs", "gridLayout add view ");
                    boolean finalHasDescription = hasDescription;
                    gridLayout.post(() -> addViewsToGridLayout(gridLayout, message, medias, mediaClickListener, mediaViewHolder, startItem, finalHasDescription));
                }
                else {
                    Log.i("my_logs", "gridLayout add view ");
                    addViewsToGridLayout(gridLayout, message, medias, mediaClickListener, mediaViewHolder, startItem, hasDescription);
                }
            }
        } else Log.i("my_logs", "gridLayout nothing to do " + gridLayout.getMessageId() + " | " + message.getId());
    }

    @BindingAdapter(value = {"messageId", "messageShouldSync"})
    public static void updateMessageId(MediaGridLayout mediaGridLayout, long messageId, boolean messageShouldSync) {
        if (!mediaGridLayout.getShouldSync()) {
            mediaGridLayout.setMessageId(messageId);
            mediaGridLayout.setShouldSync(messageShouldSync);
        }
    }

    private static void addViewsToGridLayout(final GridLayout gridLayout, final Message message, final LongSparseArray<Media> medias, MessageMediaClickListener mediaClickListener, MessageMediaViewHolder mediaViewHolder, boolean startItem, boolean hasDescription) {
        final int lastItem = medias.size() - 1;
        int columnSpec = hasDescription ? 2 : 1;
        for (int i = 0; i < lastItem; i++) {
            final int index = i;
            ItemMessageMediaGridBinding binding = DataBindingUtil.inflate(LayoutInflater.from(gridLayout.getContext()), R.layout.item_message_media_grid, null, false);

            Media media = medias.valueAt(i);
            if (FileType.image.name().equals(media.getType())) {
                binding.setImage(media);
                if (media.getDescription() != null && !media.getDescription().isEmpty())
                    setDescription(gridLayout, binding, startItem, gridLayout.getResources().getDimension(R.dimen.message_media_bottom_padding_16dp));
                binding.setVideo(null);
            } else {
                binding.setImage(null);
                binding.setVideo(media);
                if (media.getDescription() != null && !media.getDescription().isEmpty())
                    setDescription(gridLayout, binding, startItem, gridLayout.getResources().getDimension(R.dimen.message_media_bottom_padding_16dp));
                if (mediaViewHolder.getPlayerView() == null) {
                    mediaViewHolder.setPlayerView(binding.playerView);
                    mediaViewHolder.setRoundedImageView(binding.roundedImageView);
                    mediaViewHolder.setVideoContainer(binding.videoContainer);
                    mediaViewHolder.setMedia(media.getId());
                }
            }

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = hasDescription ? GridLayout.LayoutParams.MATCH_PARENT : gridLayout.getWidth() / gridLayout.getColumnCount() - layoutParams.rightMargin - layoutParams.leftMargin;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.rowSpec = GridLayout.spec(hasDescription ? i : i / 2);
            layoutParams.columnSpec = GridLayout.spec(hasDescription ? 0: i % 2, columnSpec);
            binding.getRoot().setLayoutParams(layoutParams);
            binding.setViewModel(mediaViewHolder.viewModel);
            binding.setMessage(message);
            binding.setMediaClickListener(mediaClickListener);
            binding.setCancelUploadListener((mediaMessage, media1) -> {
                if (media1.getId() <= 0) {
                    mediaClickListener.cancelUpload(mediaMessage, media1);
                    gridLayout.removeViewAt(index);
                    Log.i("my_logs", "gridLayout removeViewAt " + index);
                } else mediaClickListener.cancelDownload(mediaMessage.getMedias().get(media1.getId()));
            });
            gridLayout.addView(binding.getRoot(), index);
        }
        ItemMessageMediaGridBinding binding = DataBindingUtil.inflate(LayoutInflater.from(gridLayout.getContext()), R.layout.item_message_media_grid, null, false);

        Media media = medias.valueAt(lastItem);
        if (FileType.image.name().equals(media.getType())) {
            binding.setImage(media);
            if (media.getDescription() != null && !media.getDescription().isEmpty())
                setDescription(gridLayout, binding, startItem, gridLayout.getResources().getDimension(R.dimen.message_media_bottom_padding_40dp));
            binding.setVideo(null);
        } else {
            binding.setImage(null);
            binding.setVideo(media);
            if (media.getDescription() != null && !media.getDescription().isEmpty())
                setDescription(gridLayout, binding, startItem, gridLayout.getResources().getDimension(R.dimen.message_media_bottom_padding_40dp));
            if (mediaViewHolder.getPlayerView() == null) {
                mediaViewHolder.setPlayerView(binding.playerView);
                mediaViewHolder.setRoundedImageView(binding.roundedImageView);
                mediaViewHolder.setVideoContainer(binding.videoContainer);
                mediaViewHolder.setMedia(media.getId());
            }
        }

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = lastItem % 2 == 0 || hasDescription ? GridLayout.LayoutParams.MATCH_PARENT : gridLayout.getWidth() / gridLayout.getColumnCount() - layoutParams.rightMargin - layoutParams.leftMargin;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.rowSpec = GridLayout.spec(hasDescription ? lastItem : lastItem / 2);
        layoutParams.columnSpec = GridLayout.spec(hasDescription ? 0 : lastItem % 2, hasDescription ? columnSpec : lastItem % 2 == 0 ? 2 : 1);
        binding.getRoot().setLayoutParams(layoutParams);
        binding.setViewModel(mediaViewHolder.viewModel);
        binding.setMessage(message);
        binding.setMediaClickListener(mediaClickListener);
        binding.setCancelUploadListener((mediaMessage, media1) -> {
            if (media1.getId() <= 0) {
                mediaClickListener.cancelUpload(mediaMessage, media1);
                gridLayout.removeViewAt(lastItem);
                Log.i("my_logs", "gridLayout removeViewAt " + lastItem);
            } else mediaClickListener.cancelDownload(mediaMessage.getMedias().get(media1.getId()));
        });
        gridLayout.addView(binding.getRoot(), lastItem);
    }

    private static void setDescription(GridLayout gridLayout, ItemMessageMediaGridBinding binding, boolean startItem, float paddingBottom) {
        if (startItem) {
            binding.description.setCardBackgroundColor(gridLayout.getContext().getResources().getColor(android.R.color.white));
            binding.description.setCardElevation(1.9f);
            binding.textView79.setTextColor(gridLayout.getResources().getColor(R.color.color_text_view_dark));
            binding.roundedImageView.setElevation(2.0f);
        }
        binding.textView79.setPadding(binding.textView79.getPaddingLeft(), binding.textView79.getPaddingTop(), binding.textView79.getPaddingRight(), Math.round(paddingBottom));
    }

}
