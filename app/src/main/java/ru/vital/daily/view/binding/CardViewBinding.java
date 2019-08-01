package ru.vital.daily.view.binding;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;

import ru.vital.daily.R;
import ru.vital.daily.repository.data.Message;

public class CardViewBinding {

    @BindingAdapter(value = {"replyAnimationEnd", "message"})
    public static void setReplyAnimationEnd(CardView CardView, boolean replyAnimationEvent, Message message) {
        if (replyAnimationEvent) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(CardView, "cardBackgroundColor", new ArgbEvaluator(), CardView.getResources().getColor(R.color.colorAccent), CardView.getResources().getColor(R.color.color_message_reply_end_animation), CardView.getResources().getColor(R.color.colorAccent));
            objectAnimator.setDuration(1000);
            objectAnimator.start();
            message.setReplyAnimationEvent(false);
        }
    }

    @BindingAdapter(value = {"replyAnimationStart", "message"})
    public static void setReplyAnimationStart(CardView CardView, boolean replyAnimationEvent, Message message) {
        if (replyAnimationEvent) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofObject(CardView, "cardBackgroundColor", new ArgbEvaluator(), Color.WHITE, CardView.getResources().getColor(R.color.color_message_reply_start_animation), Color.WHITE);
            objectAnimator.setDuration(1000);
            objectAnimator.start();
            message.setReplyAnimationEvent(false);
        }
    }

}
