package ru.vital.daily.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import ru.vital.daily.R;
import ru.vital.daily.databinding.LayoutEditTextBinding;

public class EditView extends ConstraintLayout {

    public LayoutEditTextBinding binding;

    private String text;

    public EditView(Context context) {
        super(context);
        init(context);
    }

    public EditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_edit_text, this, true);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        binding.editText.setText(text);
        this.text = text;
    }
}
