package ru.vital.daily.view.binding;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import java.util.Date;

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

    @BindingAdapter("statusText")
    public static void setStatusText(TextView textView, Date date) {
        textView.setText(textView.getContext().getString(R.string.chat_status_last_online, getRelativeTimeText(textView.getContext(), date)));
    }

    private static CharSequence getRelativeTimeText(Context context, Date date) {
        return DateUtils.getRelativeDateTimeString(context, date.getTime(),
                DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
    }

}
