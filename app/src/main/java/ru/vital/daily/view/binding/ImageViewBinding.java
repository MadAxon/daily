package ru.vital.daily.view.binding;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.MediaModel;

public class ImageViewBinding {

    @BindingAdapter("clipToOutline")
    public static void setClipOutline(ImageView imageView, boolean clipToOutline) {
        imageView.setClipToOutline(clipToOutline);
    }

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    @BindingAdapter("android:src")
    public static void setScr(RoundedImageView roundedImageView, User user) {
        List<MediaModel> files = null;
        if (user != null && user.getAvatar() != null && user.getAvatar().getFiles() != null)
            files = user.getAvatar().getFiles();
        if (files != null && !files.isEmpty()) {
            if (files.size() >= 2) {
                if (files.get(0).getSize() > files.get(1).getSize())
                    loadFadingImage(roundedImageView, files.get(0).getUrl(), files.get(1).getUrl());
                else loadFadingImage(roundedImageView, files.get(1).getUrl(), files.get(0).getUrl());
            } else loadFadingImage(roundedImageView, files.get(0).getUrl(), null);
        } else {
            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .width(roundedImageView.getLayoutParams().width)
                    .height(roundedImageView.getLayoutParams().height)
                    .fontSize(40)
                    .endConfig()
                    .buildRect(getLettersForPlaceholder(user), ColorGenerator.MATERIAL.getRandomColor());
            roundedImageView.setImageDrawable(textDrawable);
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

    @BindingAdapter("simpleImage")
    public static void setSrc(RoundedImageView roundedImageView, String imageUrl) {
        loadSimpleImage(roundedImageView, imageUrl);
    }

    @BindingAdapter("android:src")
    public static void setScr(RoundedImageView roundedImageView, Drawable drawable) {
        if (drawable != null) roundedImageView.setImageDrawable(drawable);
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

    private static void loadSimpleImage(RoundedImageView imageView, String imageUrl) {
        RequestOptions requestOptions = new RequestOptions().centerCrop();
        Glide.with(imageView).load(imageUrl).apply(requestOptions).into(imageView);
    }

    private static void loadFadingImage(RoundedImageView imageView, String imageUrl, @Nullable String placeholderUrl) {
        Glide.with(imageView).load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .thumbnail(Glide.with(imageView)
                        .load(placeholderUrl)
                        .transform(new CenterCrop(), new RoundedCornersTransformation((int) imageView.getCornerRadius(), (int) imageView.getBorderWidth())))
                .transform(new CenterCrop(), new RoundedCornersTransformation((int) imageView.getCornerRadius(), (int) imageView.getBorderWidth()))
                .into(imageView);
    }

}
