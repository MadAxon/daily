package ru.vital.daily.view.binding;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import ru.vital.daily.R;

public class TextViewBinding {

    @BindingAdapter(value = {"username", "comment"})
    public static void setText(TextView textView, String username, String comment) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(username);
        spannableStringBuilder.setSpan(new TypefaceSpan("sans-serif-medium"), 0, spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString spannableString = new SpannableString(comment);
        spannableString.setSpan(new TypefaceSpan("sans-serif"), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.append("    ");
        spannableStringBuilder.append(spannableString);
        textView.setText(spannableStringBuilder);
    }

    @BindingAdapter("android:drawableStart")
    public static void setDrawableStart(TextView textView, int drawableStart) {
        textView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, 0, 0);
    }

}
