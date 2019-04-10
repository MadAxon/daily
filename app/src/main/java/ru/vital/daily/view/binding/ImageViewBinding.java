package ru.vital.daily.view.binding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class ImageViewBinding {

    @BindingAdapter("clipToOutline")
    public static void setClipOutline(ImageView imageView, boolean clipToOutline) {
        imageView.setClipToOutline(clipToOutline);
    }

    @BindingAdapter("app:srcCompat")
    public static void setSrcCompat(ImageView imageView, Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

}
