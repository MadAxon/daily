package ru.vital.daily.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import ru.vital.daily.R;
import ru.vital.daily.databinding.LayoutEditTextBinding;

public class EditView extends ConstraintLayout {

    public LayoutEditTextBinding binding;

    private String text, hint;

    private Integer inputType, maxLength, maxLines;

    private boolean showSearchIcon, focusable, enabled;

    public EditView(Context context) {
        super(context);
        init(context);
    }

    public EditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public EditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.EditView, 0, 0);
        hint = typedArray.getString(R.styleable.EditView_android_hint);
        text = typedArray.getString(R.styleable.EditView_android_text);
        inputType = typedArray.getInteger(R.styleable.EditView_android_inputType, InputType.TYPE_CLASS_TEXT);
        showSearchIcon = typedArray.getBoolean(R.styleable.EditView_showSearchIcon, true);
        focusable = typedArray.getBoolean(R.styleable.EditView_android_focusable, true);
        maxLength = typedArray.getInteger(R.styleable.EditView_android_maxLength, 0);
        maxLines = typedArray.getInteger(R.styleable.EditView_android_maxLines, 0);
        enabled = typedArray.getBoolean(R.styleable.EditView_android_enabled, true);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        //LayoutInflater.from(context).inflate(R.layout.layout_edit_text, this, true);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_edit_text, this, true);
        binding.editText.setInputType(inputType);
        if (inputType == InputType.TYPE_CLASS_PHONE)
            binding.editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.getDefault().getCountry()));
        binding.editText.setText(text);
        binding.editText.setHint(hint);
        binding.editText.setTypeface(Typeface.create("roboto_regular", Typeface.NORMAL));
        binding.editText.setEnabled(enabled);

        binding.setShowSearchIcon(showSearchIcon);
        binding.setFocusable(focusable);
        binding.setEditTextClickListener(v -> this.performClick());
        if (maxLength != 0)
            binding.editText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(maxLength)
            });
        if (maxLines != 0)
            binding.editText.setMaxLines(maxLines);

        binding.setClearClickListener(v -> setText(""));
    }

    public String getText() {
        text = binding.editText.getText().toString();
        return text;
    }

    public void setText(String text) {
        binding.editText.setText(text);
        this.text = text;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        binding.editText.setHint(hint);
        this.hint = hint;
    }

    public Integer getInputType() {
        return inputType;
    }

    public void setInputType(Integer inputType) {
        binding.editText.setInputType(inputType);
        this.inputType = inputType;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
