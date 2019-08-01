package ru.vital.daily.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.vital.daily.R;

public class ProgressView extends LinearLayout {

    private static final String TAG = ProgressView.class.getSimpleName();

    private final LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
    private final LayoutParams SPACE_LAYOUT_PARAM = new LayoutParams(5, LayoutParams.WRAP_CONTENT);

    private final List<PausableProgressBar> progressBars = new ArrayList<>();

    private int storiesCount = -1;
    /**
     * pointer of running animation
     */
    private int current = 0;
    private StoriesListener storiesListener;
    boolean isComplete;

    private boolean isSkipStart;
    private boolean isReverseStart;
    private boolean isRunning;
    private boolean isPaused;

    private int transparentColor, solidColor;

    public interface StoriesListener {
        void onNext();

        void onPrev();

        void onComplete();
    }

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        storiesCount = typedArray.getInt(R.styleable.ProgressView_progressCount, 0);
        transparentColor = typedArray.getColor(R.styleable.ProgressView_transparentColor, context.getResources().getColor(R.color.color_progress_view_background));
        solidColor = typedArray.getColor(R.styleable.ProgressView_solidColor, context.getResources().getColor(android.R.color.white));
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < storiesCount; i++) {
            final PausableProgressBar p = createProgressBar();
            p.setTransparentColor(transparentColor);
            p.setSolidColor(solidColor);
            progressBars.add(p);
            addView(p);
            if ((i + 1) < storiesCount) {
                addView(createSpace());
            }
        }
    }

    private PausableProgressBar createProgressBar() {
        PausableProgressBar p = new PausableProgressBar(getContext());
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }

    public void setStories(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
        for (int i = 0; i < progressBars.size(); i++) {
            //progressBars.get(i).setDuration(durations[i]);
            progressBars.get(i).setCallback(callback());
        }
    }

    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    public void setStoriesListener(StoriesListener storiesListener) {
        this.storiesListener = storiesListener;
    }

    /**
     * Skip current story
     */
    public void skip() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        PausableProgressBar p = progressBars.get(current);
        isSkipStart = true;
        p.setMax();
    }

    /**
     * Reverse current story
     */
    public void reverse() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        PausableProgressBar p = progressBars.get(current);
        isReverseStart = true;
        p.setMin();
    }

    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    public void setStoryDuration(long duration) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(duration);
            progressBars.get(i).setCallback(callback());
        }
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    public void setStoriesCountWithDurations(@NonNull long[] durations) {
        storiesCount = durations.length;
        bindViews();
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(durations[i]);
            progressBars.get(i).setCallback(callback());
        }
    }

    public void setCurrentStoryDuration(long duration) {
        progressBars.get(current).setDuration(duration);
        //progressBars.get(index).setCallback(callback(index));
    }

    private PausableProgressBar.Callback callback() {
        return new PausableProgressBar.Callback() {
            @Override
            public void onStartProgress() {
                isRunning = true;
            }

            @Override
            public void onFinishProgress() {
                isRunning = false;
                Log.i("my_logs", "onFinishProgress " + current);
                if (isReverseStart) {
                    if (0 <= (current - 1)) {
                        PausableProgressBar p = progressBars.get(current - 1);
                        p.setMinWithoutCallback();
                        current = current - 1;
                        //progressBars.get(--current).startDownload();
                    } else {
                        //progressBars.get(current).startDownload();
                    }
                    isReverseStart = false;
                    if (storiesListener != null) storiesListener.onPrev();
                    return;
                }
                isSkipStart = false;
                if (current < (progressBars.size() - 1)) {
                    current = current + 1;
                    //progressBars.get(current).startDownload();
                    if (storiesListener != null) storiesListener.onNext();
                } else {
                    isComplete = true;
                    if (storiesListener != null) storiesListener.onComplete();
                }
            }
        };
    }

    /**
     * Start progress animation
     */
    public void startStories() {
        progressBars.get(0).startProgress();
    }

    /**
     * Start progress animation from specific progress
     */
    public void setInitialStory(int from) {
        for (int i = 0; i < from; i++) {
            progressBars.get(i).setMaxWithoutCallback();
        }
        current = from;
        //progressBars.get(from).startDownload();
    }

    public void startCurrentStory() {
        if (!isPaused && !isRunning) progressBars.get(current).startProgress();
        Log.i("my_logs", "startDownload " + current);
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        for (PausableProgressBar p : progressBars) {
            p.clear();
        }
        isRunning = false;
        isComplete = false;
    }

    /**
     * Pause story
     */
    public void pause() {
        if (current < 0) return;
        progressBars.get(current).pauseProgress();
        isPaused = true;
    }

    /**
     * Resume story
     */
    public void resume() {
        if (current < 0) return;
        if (isPaused && !isRunning) {
            progressBars.get(current).startProgress();
            Log.i("my_logs", "resume_startProgress");
        } else {
            progressBars.get(current).resumeProgress();
            Log.i("my_logs", "resume_resumeProgress");
        }
        isPaused = false;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}
