package ru.vital.daily.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import io.reactivex.functions.Consumer;
import ru.vital.daily.R;
import ru.vital.daily.databinding.ViewDialogMessageDeleteBinding;

public class DeleteMessageDialog extends AlertDialog {

    private final ViewDialogMessageDeleteBinding binding;

    private final int messagesSize;

    private final Consumer<Boolean> forAll;

    public DeleteMessageDialog(@NonNull Context context, int themeResId, int messagesSize, String username, Consumer<Boolean> forAll) {
        super(context, themeResId);
        binding = getBinding(context);
        this.messagesSize = messagesSize;
        binding.setUsername(username);
        this.forAll = forAll;
    }

    private ViewDialogMessageDeleteBinding getBinding(Context context) {
        return DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_dialog_message_delete, null, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setView(binding.getRoot(), 0 , 0, 0, 0);
        setTitle(getContext().getString(R.string.chat_message_delete_title, getContext().getResources().getQuantityString(R.plurals.messages, messagesSize, messagesSize)));
        setCancelable(true);
        setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(R.string.common_delete), (dialog, which) -> {
            try {
                forAll.accept(binding.checkbox.isChecked());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel), (dialog, which) -> dismiss());
        setOnKeyListener((dialog, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
        super.onCreate(savedInstanceState);

    }


}
