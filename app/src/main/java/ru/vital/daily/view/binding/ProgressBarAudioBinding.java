package ru.vital.daily.view.binding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import ru.vital.daily.view.ProgressBarAudio;

@InverseBindingMethods({
        @InverseBindingMethod(type = ProgressBarAudio.class, attribute = "current")
})
public class ProgressBarAudioBinding {

    @BindingAdapter(value = "currentAttrChanged")
    public static void setListener(ProgressBarAudio progressBar, InverseBindingListener inverseBindingListener) {
        progressBar.getObjectAnimator().addUpdateListener(animation -> {
            progressBar.setCurrent((int) (animation.getDuration() - animation.getCurrentPlayTime()));
            inverseBindingListener.onChange();
        });
        progressBar.getObjectAnimator().addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progressBar.setCurrent((int) animation.getDuration());
                inverseBindingListener.onChange();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                progressBar.setCurrent((int) animation.getDuration());
                inverseBindingListener.onChange();
            }
        });
        progressBar.setListener(current -> {
            progressBar.setCurrent(current);
            inverseBindingListener.onChange();
        });
    }

    @BindingAdapter("current")
    public static void setCurrent(ProgressBarAudio progressBarAudio, int current) {
        if (progressBarAudio.getCurrent() != current) progressBarAudio.setCurrent(current);
    }

    @InverseBindingAdapter(attribute = "current")
    public static int getCurrent(ProgressBarAudio progressBarAudio) {
        return progressBarAudio.getCurrent();
    }

    @BindingAdapter("playing")
    public static void setPlaying(ProgressBarAudio progressBar, Boolean playing) {
        progressBar.setPlaying(playing);
    }

    @BindingAdapter("audioDuration")
    public static void setAudioDuration(ProgressBarAudio progressBar, int duration) {
        if (duration != 0) {
            progressBar.setDuration(duration);
            progressBar.getListener().initCurrent(duration);
        }
    }

}
