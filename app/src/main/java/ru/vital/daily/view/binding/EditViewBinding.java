package ru.vital.daily.view.binding;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import ru.vital.daily.view.EditView;

@InverseBindingMethods({
        @InverseBindingMethod(type = EditView.class, attribute = "text")
})
public class EditViewBinding {

    @BindingAdapter(value = "textAttrChanged")
    public static void setListener(EditView editView, final InverseBindingListener inverseBindingListener) {
        if (inverseBindingListener != null)
            editView.binding.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editView.binding.setContainsText(s.length() != 0);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    inverseBindingListener.onChange();
                }
            });
    }

    @BindingAdapter("text")
    public static void setText(EditView editView, String text) {
        if (text != null && !text.equals(editView.getText())) editView.setText(text);
    }

    @InverseBindingAdapter(attribute = "text")
    public static String getText(EditView editView) {
        return editView.getText();
    }

}
