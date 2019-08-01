package ru.vital.daily.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class MediaGridLayout extends GridLayout {
    
    private long messageId;
    
    private boolean shouldSync;
    
    public MediaGridLayout(Context context) {
        super(context);
    }

    public MediaGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MediaGridLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean getShouldSync() {
        return shouldSync;
    }

    public void setShouldSync(boolean shouldSync) {
        this.shouldSync = shouldSync;
    }
}
