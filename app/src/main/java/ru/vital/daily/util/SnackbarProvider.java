package ru.vital.daily.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import ru.vital.daily.R;

public final class SnackbarProvider {

    public static Snackbar getWarnSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view
                .getContext(), R.color.color_snackbar_background_warn));
        return snackbar;
    }

    public static Snackbar getSuccessSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view
                .getContext(), R.color.color_snackbar_background_success));
        return snackbar;
    }

    public static Snackbar getSimpleSnackbar(View view, String message) {
        return Snackbar.make(view, message, Snackbar.LENGTH_LONG);
    }

}
