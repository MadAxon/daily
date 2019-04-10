package ru.vital.daily.repository.model;

import android.graphics.drawable.Drawable;

public class FragmentSheetModel {

    private final int drawableId, stringId;

    public FragmentSheetModel(int drawableId, int stringId) {
        this.drawableId = drawableId;
        this.stringId = stringId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public int getStringId() {
        return stringId;
    }
}
