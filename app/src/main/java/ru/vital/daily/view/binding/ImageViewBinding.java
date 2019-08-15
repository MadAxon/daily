package ru.vital.daily.view.binding;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.InputStream;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.OkHttpClient;
import ru.vital.daily.R;
import ru.vital.daily.enums.ChatType;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.repository.api.ProgressInterceptor;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.FileSize;
import ru.vital.daily.util.FileUtil;

public class ImageViewBinding {

    @BindingAdapter("android:layout_marginBottom")
    public static void setLayoutMarginBottom(RoundedImageView roundedImageView, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) roundedImageView.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, Math.round(margin));
    }

    @BindingAdapter("clipToOutline")
    public static void setClipOutline(ImageView imageView, boolean clipToOutline) {
        imageView.setClipToOutline(clipToOutline);
    }

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView imageView, int drawable) {
        imageView.setImageResource(drawable);
    }

    @BindingAdapter("android:src")
    public static void setScr(RoundedImageView roundedImageView, User user) {
        if (user != null && !setScr(roundedImageView, user.getAvatar())) {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .width(roundedImageView.getLayoutParams().width)
                    .height(roundedImageView.getLayoutParams().height)
                    .fontSize((int) (roundedImageView.getLayoutParams().width / 2.25))
                    .endConfig()
                    .buildRect(getLettersForPlaceholder(user), ColorGenerator.MATERIAL.getColor(user.getId()));
            roundedImageView.setImageDrawable(textDrawable);
        }
    }

    private static boolean setScr(RoundedImageView roundedImageView, Media image) {
        if (image != null && image.getFiles() != null && image.getFiles().size() > 0) {
            /*MediaModel placeholder = null, target = null;
            for (MediaModel mediaModel : image.getFiles()) {
                if (placeholder == null) placeholder = target = mediaModel;
                else {
                    if (mediaModel.getSize() > target.getSize()) target = mediaModel;
                    else if (mediaModel.getSize() < placeholder.getSize()) placeholder = mediaModel;
                }
            }
            if (FileUtil.exists(target.getUrl()))
                loadSimpleImage(roundedImageView, target.getUrl());
            else if (uploadLimit == 0 || target.getSize() <= uploadLimit) {
                loadFadingImage(roundedImageView, target.getUrl(), placeholder.getUrl());
            } else {
                loadSimpleImage(roundedImageView, placeholder.getUrl());
            }*/
            if (image.getFiles().size() >= 2)
                loadFadingImage(roundedImageView, image.getFiles().get(0).getUrl(), image.getFiles().get(image.getFiles().size() - 1).getUrl());
            else loadSimpleImage(roundedImageView, image.getFiles().get(0).getUrl());
            return true;
        }
        return false;
    }

    @BindingAdapter(value = {"message_image", "message_video", "message_media_uploaded"}, requireAll = false)
    public static void setMessageMedia(RoundedImageView roundedImageView, Media image, Media video, boolean uploaded) {
        if (image != null && image.getFiles() != null && image.getFiles().size() > 0) {
            Log.i("my_logs", "message_image | " + image.getFiles().get(0).getUrl());
            if (uploaded || FileUtil.exists(image.getFiles().get(0).getUrl()))
                loadSimpleImage(roundedImageView, image.getFiles().get(0).getUrl());
            else if (image.getFiles().get(0).getSize() <= FileSize.MB_LIMIT_IMAGE_FOR_AUTO_UPLOAD) {
                loadFadingImage(roundedImageView, image.getFiles().get(0).getUrl(), image.getFiles().get(image.getFiles().size() - 1).getUrl());
                image.setHasIconForProgress(false);
            } else {
                loadSimpleImage(roundedImageView, image.getFiles().get(image.getFiles().size() - 1).getUrl());
                image.setHasIconForProgress(true);
            }
        } else if (video != null && video.getFiles() != null && video.getFiles().size() > 0) {
            if (video.getFiles().size() >= 1) {
                loadSimpleImage(roundedImageView, video.getFiles().get(video.getFiles().size() - 1).getUrl());
                if (!FileUtil.exists(video.getFiles().get(0).getUrl())) {
                    Log.i("my_logs", "video.setHasIconForProgress");
                    video.setHasIconForProgress(true);
                }
            }
            /*for (MediaModel mediaModel : video.getFiles()) {
                if (mediaModel.getType().contains(FileType.image.name())) {
                    loadSimpleImage(roundedImageView, mediaModel.getUrl());
                    darkenImage(roundedImageView);
                    return;
                }
            }
            if (video.getFiles().size() > 0) {
                loadSimpleImage(roundedImageView, video.getFiles().get(0).getUrl());
                darkenImage(roundedImageView);
            }*/
        }
    }

    @BindingAdapter(value = {"placeholder", "uri", "textPlaceholder"}, requireAll = false)
    public static void setScr(RoundedImageView roundedImageView, Drawable placeholder, Uri uri, String textPlaceholder) {
        if (uri != null) loadSimpleImage(roundedImageView, uri.toString());
        else if (placeholder != null) roundedImageView.setImageDrawable(placeholder);
        else if (textPlaceholder != null && !textPlaceholder.isEmpty()) {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .width(roundedImageView.getLayoutParams().width)
                    .height(roundedImageView.getLayoutParams().height)
                    .fontSize(40)
                    .endConfig()
                    .buildRect(String.valueOf(textPlaceholder.charAt(0)), ColorGenerator.MATERIAL.getRandomColor());
            roundedImageView.setImageDrawable(textDrawable);
        }
    }

    @BindingAdapter(value = {"message_user", "message_readAt", "message_status"}, requireAll = false)
    public static void setMessageReadStatus(RoundedImageView roundedImageView, User user, Date readAt, Boolean sendStatus) {
        if (readAt == null) {
            if (sendStatus == null) {
                roundedImageView.setBorderWidth(roundedImageView.getContext().getResources().getDimension(R.dimen.message_item_read_status_no_padding));
                setScr(roundedImageView, roundedImageView.getResources().getDrawable(R.drawable.ic_access_time));
            } else if (sendStatus) {
                roundedImageView.setBorderWidth(roundedImageView.getContext().getResources().getDimension(R.dimen.message_item_read_status_no_padding));
                setScr(roundedImageView, roundedImageView.getResources().getDrawable(R.drawable.ic_check_circle_outline_white));
            } else {
                roundedImageView.setBorderWidth(roundedImageView.getContext().getResources().getDimension(R.dimen.message_item_read_status_no_padding));
                setScr(roundedImageView, roundedImageView.getResources().getDrawable(R.drawable.ic_error));
            }
        } else {
            roundedImageView.setBorderWidth(roundedImageView.getContext().getResources().getDimension(R.dimen.message_item_read_status_padding));
            setScr(roundedImageView, user);
        }
    }

    @BindingAdapter("simpleImage")
    public static void setSrc(RoundedImageView roundedImageView, String imageUrl) {
        loadSimpleImage(roundedImageView, imageUrl);
    }

    @BindingAdapter("simpleImage")
    public static void setSrc(PhotoView photoView, String imageUrl) {
        loadSimpleImage(photoView, imageUrl);
    }

    @BindingAdapter("android:src")
    public static void setScr(RoundedImageView roundedImageView, Drawable drawable) {
        if (drawable != null) roundedImageView.setImageDrawable(drawable);
    }

    @BindingAdapter("selected")
    public static void setSelected(RoundedImageView roundedImageView, boolean selected) {
        roundedImageView.setBorderColor(selected ? roundedImageView.getResources().getColor(R.color.colorAccent) : Color.WHITE);
    }

    @BindingAdapter(value = {"chat_avatar_cover", "chat_avatar_member"})
    public static void setChatAvatar(RoundedImageView roundedImageView, Chat chat, User member) {
        if (chat != null)
            if (!ChatType.dialog.name().equals(chat.getType())) {
                if (chat.getCover() != null)
                    setScr(roundedImageView, chat.getCover());
                else {
                    TextDrawable textDrawable = TextDrawable.builder()
                            .beginConfig()
                            .width(roundedImageView.getLayoutParams().width)
                            .height(roundedImageView.getLayoutParams().height)
                            .fontSize(40)
                            .endConfig()
                            .buildRect(chat.getName().substring(0, 1), ColorGenerator.MATERIAL.getColor(chat.getId()));
                    roundedImageView.setImageDrawable(textDrawable);
                }
            } else setScr(roundedImageView, member);

    }

    @BindingAdapter(value = {"mediaReplyAnimation", "message"})
    public static void setMediaReplyAnimation(RoundedImageView roundedImageView, boolean replyAnimation, Message message) {
        if (replyAnimation) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(roundedImageView, "borderColor", new ArgbEvaluator(), roundedImageView.getBorderColor(), roundedImageView.getResources().getColor(R.color.color_message_reply_end_animation), roundedImageView.getBorderColor());
            objectAnimator.setDuration(1000);
            objectAnimator.start();
            message.setReplyAnimationEvent(false);
        }
    }

    private static String getLettersForPlaceholder(User user) {
        if (user != null) {
            if (user.getFirstname() != null && !user.getFirstname().isEmpty() && user.getLastname() != null && !user.getLastname().isEmpty())
                return String.format("%s%s", user.getFirstname().charAt(0), user.getLastname().charAt(0));
            else if (user.getUname() != null && !user.getUname().isEmpty())
                return String.valueOf(user.getUname().charAt(0));
        }
        return "@";
    }

    private static void loadSimpleImage(ImageView imageView, String imageUrl) {
        RequestOptions requestOptions = new RequestOptions().centerCrop();
        Glide.with(imageView).load(imageUrl).apply(requestOptions).into(imageView);
    }

    private static void loadFadingImage(RoundedImageView imageView, String imageUrl, @Nullable String placeholderUrl) {
        RequestOptions requestOptions = new RequestOptions().centerCrop();
        int radius = Math.round(imageView.getCornerRadius());
        int borderWidth = Math.round(imageView.getBorderWidth());
        Glide.with(imageView).load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .apply(requestOptions)
                .thumbnail(Glide.with(imageView)
                        .load(placeholderUrl)
                        .transform(new CenterCrop(), new RoundedCornersTransformation(radius, borderWidth)))
                .transform(new CenterCrop(), new RoundedCornersTransformation(radius, borderWidth))
                .into(imageView);
    }

    private static void darkenImage(RoundedImageView roundedImageView) {
        roundedImageView.setColorFilter(Color.rgb(192, 192, 192), PorterDuff.Mode.MULTIPLY);
    }

}
