package ru.vital.daily.enums;

import android.graphics.Typeface;

import ru.vital.daily.R;

public enum TypefaceEnum {

    classic("sans-serif-regular", R.string.paint_text_classic, Typeface.NORMAL),
    bold("sans-serif-regular", R.string.paint_text_bold, Typeface.BOLD),
    italic("sans-serif-regular", R.string.paint_text_italic, Typeface.ITALIC),
    bold_italic("sans-serif-regular", R.string.paint_text_bold_italic, Typeface.BOLD_ITALIC);
    //neon("sans-serif-regular", R.string.paint_text_neon, Typeface.NORMAL);

    public String familyName;

    public int style, familyStringResIs;

    TypefaceEnum(String familyName, int familyStringResIs, int style) {
        this.familyName = familyName;
        this.familyStringResIs = familyStringResIs;
        this.style = style;
    }

    public static TypefaceEnum valueOf(int style) {
        for (TypefaceEnum typefaceEnum : values()) {
            if (typefaceEnum.style == style) return typefaceEnum;
        }
        return classic;
    }
}
