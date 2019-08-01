package ru.vital.daily.view.binding;

import android.util.Log;
import android.view.View;

import com.pnikosis.materialishprogress.ProgressWheel;

import androidx.databinding.BindingAdapter;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.model.MediaModel;

public class ProgressWheelBinding {

    @BindingAdapter("mediaProgress")
    public static void setMediaProgress(ProgressWheel progressWheel, Float progress) {
        if (progress == null) {
            progressWheel.setVisibility(View.GONE);
        } else if (progress == 0) {
            progressWheel.spin();
            progressWheel.setVisibility(View.VISIBLE);
        } else {
            if (progressWheel.getVisibility() == View.GONE) {
                progressWheel.spin();
                progressWheel.setVisibility(View.VISIBLE);
            }
            progressWheel.setProgress(progress);
        }
    }

}
