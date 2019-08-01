package ru.vital.daily.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

public class ProgressBarAudio extends ProgressBar {

    private final ObjectAnimator objectAnimator = new ObjectAnimator();

    private Listener listener;

    private int current;

    public ProgressBarAudio(Context context) {
        super(context);
        initAnimator();
    }

    public ProgressBarAudio(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnimator();
    }

    public ProgressBarAudio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimator();
    }

    public ProgressBarAudio(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAnimator();
    }

    private void initAnimator() {
        objectAnimator.setDuration(0);
        objectAnimator.setTarget(this);
        objectAnimator.setPropertyName("progress");
        objectAnimator.setInterpolator(new LinearInterpolator());
    }

    public void setPlaying(Boolean playing) {
        if (objectAnimator.getDuration() != 0) {
            if (playing == null) {
                objectAnimator.cancel();
                setProgress(0);
            } else if (playing) {
                if (objectAnimator.isStarted() && objectAnimator.isPaused())
                    objectAnimator.resume();
                else objectAnimator.start();
            } else {
                objectAnimator.pause();
            }
        }
    }

    public void setDuration(int duration) {
        setMax(duration * 100);
        setProgress(0);
        objectAnimator.setDuration(duration);
        objectAnimator.setIntValues(0, duration * 100);
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
        //Log.i("my_logs", "progress current " + current);
    }

    public ObjectAnimator getObjectAnimator() {
        return objectAnimator;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {

        void initCurrent(int current);

    }
}
