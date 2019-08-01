package ru.vital.daily.util;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatItemDecoration extends RecyclerView.ItemDecoration {

    private final int padding;

    public ChatItemDecoration(float padding) {
        this.padding = (int) padding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = padding;
        outRect.bottom = padding;
    }
}
