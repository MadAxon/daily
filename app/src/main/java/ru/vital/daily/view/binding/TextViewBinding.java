package ru.vital.daily.view.binding;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import com.google.android.exoplayer2.C;

import ru.vital.daily.R;
import ru.vital.daily.enums.FileType;
import ru.vital.daily.enums.MessageContentType;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.MediaModel;

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

    @BindingAdapter("forwardAuthor")
    public static void setForwardAuthor(TextView textView, User forwardAuthor) {
        if (forwardAuthor != null) {
            textView.setVisibility(View.VISIBLE);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_forward));
            SpannableString spannableString = new SpannableString(forwardAuthor.getUname());
            spannableString.setSpan(new TypefaceSpan("sans-serif-medium"), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(spannableString);
            textView.setText(spannableStringBuilder);
        } else textView.setVisibility(View.GONE);
    }

    @BindingAdapter("android:drawableStart")
    public static void setDrawableStart(TextView textView, int drawableStart) {
        textView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, 0, 0, 0);
    }

    @BindingAdapter(value = {"createdAt", "updatedAt"})
    public static void setMessageDateInfo(TextView textView, Date createdAt, @Nullable Date updatedAt) {
        if (createdAt != null) {
            if (updatedAt == null || updatedAt.getTime() - createdAt.getTime() <= 3000L)
                textView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(createdAt));
            else
                textView.setText(textView.getContext().getString(R.string.chat_message_updated, new SimpleDateFormat("HH:mm", Locale.getDefault()).format(createdAt)));
        }
    }

    @BindingAdapter("mediaDate")
    public static void setMediaDate(TextView textView, Date date) {
        if (date != null)
            textView.setText(DateUtils.formatDateTime(textView.getContext(), date.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
    }

    @BindingAdapter("simpleDate")
    public static void setSimpleDate(TextView textView, Date date) {
        if (date != null)
            textView.setText(new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date));
    }

    @BindingAdapter("relativeSimpleTime")
    public static void setRelativeSimpleTime(TextView textView, Date date) {
        if (date != null)
            textView.setText(getRelativeTimeText(textView.getContext(), date));
        else textView.setText(null);
    }

    @BindingAdapter(value = {"relativeOnlineTime", "typing", "online"}, requireAll = false)
    public static void setRelativeOnlineTime(TextView textView, Date date, boolean typing, boolean online) {
        if (typing)
            textView.setText(R.string.chat_message_typing);
        else if (online)
            textView.setText(textView.getResources().getString(R.string.chat_status_online));
        else if (date != null)
            textView.setText(textView.getContext().getString(R.string.chat_status_last_online, getRelativeTimeText(textView.getContext(), date)));
        else textView.setText(null);
    }

    @BindingAdapter("videoDuration")
    public static void setVideoDuration(TextView textView, MediaModel mediaModel) {
        if (mediaModel != null && mediaModel.getType().contains("video") && mediaModel.getUrl() != null) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(mediaModel.getUrl());
                retriever.setDataSource(fileInputStream.getFD());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //retriever.setDataSource(textView.getContext(), Uri.parse(mediaModel.getUrl()));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (time != null) {
                long seconds = Long.parseLong(time) / 1000;
                retriever.release();
                textView.setText(String.format(Locale.getDefault(), "%02d:%02d", (seconds / 60) % 60, seconds % 60));
            } else textView.setText(null);
        } else textView.setText(null);
    }

    @BindingAdapter("audioCurrentSeek")
    public static void setAudioDuration(TextView textView, int audioCurrentSeek) {
        int seconds = audioCurrentSeek / 1000;
        textView.setText(String.format(Locale.getDefault(),"%02d:%02d", (seconds / 60) % 60, seconds % 60));
    }

    @BindingAdapter(value = {"chatLastMessage", "typing"}, requireAll = false)
    public static void setChatLastMessage(TextView textView, Message message, boolean typing) {
        if (typing) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_typing));
            spannableStringBuilder.setSpan(new ForegroundColorSpan(textView.getResources().getColor(R.color.colorAccent)), 0, spannableStringBuilder.length(), 0);
            textView.setText(spannableStringBuilder);
            return;
        }
        if (message == null) {
            textView.setText(null);
            return;
        }
        SpannableStringBuilder spannableStringBuilder;
        if (message.getId() != 0) {
            if (message.getText() != null && !message.getText().isEmpty()) {
                spannableStringBuilder = new SpannableStringBuilder(message.getText());
                spannableStringBuilder.setSpan(new ForegroundColorSpan(textView.getResources().getColor(R.color.color_text_view_light)), 0, spannableStringBuilder.length(), 0);
            } else if (message.getMedias() != null && message.getMedias().size() > 0) {
                final String type = message.getMedias().valueAt(0).getType();
                if (FileType.image.name().equals(type))
                    spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_photo));
                else if (FileType.video.name().equals(type))
                    spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_video));
                else if (FileType.voice.name().equals(type))
                    spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_voice));
                else spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_file));
                spannableStringBuilder.setSpan(new ForegroundColorSpan(textView.getResources().getColor(R.color.colorAccent)), 0, spannableStringBuilder.length(), 0);
            } else if (MessageContentType.contact.name().equals(message.getContentType()) || message.getAccountId() != null && message.getAccountId() > 0) {
                spannableStringBuilder = new SpannableStringBuilder(textView.getResources().getString(R.string.chat_message_contact));
                spannableStringBuilder.setSpan(new ForegroundColorSpan(textView.getResources().getColor(R.color.colorAccent)), 0, spannableStringBuilder.length(), 0);
            } else spannableStringBuilder = new SpannableStringBuilder();
        } else {
            spannableStringBuilder = new SpannableStringBuilder(textView.getContext().getString(R.string.chat_message_draft));
            spannableStringBuilder.setSpan(new ForegroundColorSpan(textView.getResources().getColor(R.color.color_text_view_warn)), 0, spannableStringBuilder.length(), 0);
            SpannableString spannableString = new SpannableString(message.getText());
            spannableStringBuilder.append(spannableString);
        }
        textView.setText(spannableStringBuilder);
    }

    @BindingAdapter("android:layout_marginStart")
    public static void setMarginStart(TextView textView, float margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        layoutParams.setMargins(Math.round(margin), layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
    }

    @BindingAdapter("android:paddingBottom")
    public static void setPaddingBottom(TextView textView, float padding) {
        textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), Math.round(padding));
    }

    @BindingAdapter(value = {"subtitle_text", "subtitle_string_id"})
    public static void setSubtitle(TextView textView, String text, Integer stringId) {
        if (stringId != null) textView.setText(stringId);
        else textView.setText(text);
    }

    private static CharSequence getRelativeTimeText(Context context, Date date) {
        return DateUtils.getRelativeDateTimeString(context, date.getTime(),
                DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
    }

}
