package ru.vital.daily.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import ru.vital.daily.R;
import ru.vital.daily.databinding.LayoutPausableProgressBinding;

final class PausableProgressBar extends FrameLayout {

    private static final int DEFAULT_PROGRESS_DURATION = 5000;

    private PausableScaleAnimation animation;
    private long duration = DEFAULT_PROGRESS_DURATION;
    private Callback callback;

    private final LayoutPausableProgressBinding binding;

    interface Callback {
        void onStartProgress();
        void onFinishProgress();
    }

    public PausableProgressBar(Context context) {
        this(context, null);
    }

    public PausableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PausableProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_pausable_progress, this, true);
    }

    public void setTransparentColor(int transparentColor) {
        binding.backProgress.setBackgroundResource(transparentColor);
    }

    public void setSolidColor(int solidColor) {
        binding.frontProgress.setBackgroundColor(solidColor);
        binding.maxProgress.setBackgroundColor(solidColor);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCallback(@NonNull Callback callback) {
        this.callback = callback;
    }

    void setMax() {
        Log.i("my_logs", "setMax");
        finishProgress(true);
    }

    void setMin() {
        Log.i("my_logs", "setMin");
        finishProgress(false);
    }

    void setMinWithoutCallback() {
        binding.maxProgress.setBackgroundResource(R.color.color_progress_view_background);

        binding.maxProgress.setVisibility(VISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }

    void setMaxWithoutCallback() {
        binding.maxProgress.setBackgroundResource(android.R.color.white);

        binding.maxProgress.setVisibility(VISIBLE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
        }
    }

    private void finishProgress(boolean isMax) {
        Log.i("my_logs", "finishProgress");
        if (isMax) binding.maxProgress.setBackgroundResource(android.R.color.white);
        binding.maxProgress.setVisibility(isMax ? VISIBLE : GONE);
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            if (callback != null) {
                callback.onFinishProgress();
            }
        } else {
            if (callback != null) {
                callback.onFinishProgress();
            }
        }
    }

    public void startProgress() {
        Log.i("my_logs", "startProgress");
        binding.maxProgress.setVisibility(GONE);

        animation = new PausableScaleAnimation(0, 1, 1, 1, Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.frontProgress.setVisibility(View.VISIBLE);
                if (callback != null) callback.onStartProgress();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (callback != null) callback.onFinishProgress();
                if (PausableProgressBar.this.animation != null) PausableProgressBar.this.animation.setAnimationListener(null);
                //PausableProgressBar.this.animation.cancel();
            }
        });
        animation.setFillAfter(true);
        binding.frontProgress.startAnimation(animation);
    }

    public void pauseProgress() {
        if (animation != null) {
            animation.pause();
        }
    }

    public void resumeProgress() {
        if (animation != null) {
            animation.resume();
        }
    }

    void clear() {
        if (animation != null) {
            animation.setAnimationListener(null);
            animation.cancel();
            animation = null;
        }
    }

    private class PausableScaleAnimation extends ScaleAnimation {

        private long mElapsedAtPause = 0;
        private boolean mPaused = false;

        PausableScaleAnimation(float fromX, float toX, float fromY,
                               float toY, int pivotXType, float pivotXValue, int pivotYType,
                               float pivotYValue) {
            super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
                    pivotYValue);
        }

        @Override
        public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
            if (mPaused && mElapsedAtPause == 0) {
                mElapsedAtPause = currentTime - getStartTime();
            }
            if (mPaused) {
                setStartTime(currentTime - mElapsedAtPause);
            }
            return super.getTransformation(currentTime, outTransformation, scale);
        }

        /***
         * pause animation
         */
        void pause() {
            if (mPaused) return;
            mElapsedAtPause = 0;
            mPaused = true;
        }

        /***
         * resume animation
         */
        void resume() {
            mPaused = false;
        }
    }
}
