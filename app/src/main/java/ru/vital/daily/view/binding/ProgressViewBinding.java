package ru.vital.daily.view.binding;

import androidx.databinding.BindingAdapter;
import ru.vital.daily.view.ProgressView;

public class ProgressViewBinding {

    @BindingAdapter("resumeOrPause")
    public static void resumeOrPause(ProgressView progressView, boolean resume) {
        if (resume)
            progressView.resume();
        else progressView.pause();
    }

    @BindingAdapter("duration")
    public static void setDuration(ProgressView progressView, long duration) {
        if (duration != 0) progressView.setCurrentStoryDuration(duration);
    }

}
