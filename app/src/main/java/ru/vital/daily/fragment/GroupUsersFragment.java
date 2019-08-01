package ru.vital.daily.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.adapter.ContactAdapter;
import ru.vital.daily.databinding.FragmentGroupUsersBinding;
import ru.vital.daily.fragment.sheet.SimpleSheetFragment;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.util.BitmapHandler;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.view.model.GroupUsersViewModel;

public class GroupUsersFragment extends BaseFragment<GroupUsersViewModel, FragmentGroupUsersBinding> {

    private final int
            REQUEST_AVATAR_CODE = 101,
            REQUEST_PHOTO_CODE = 102,
            REQUEST_GALLERY_CODE = 103,
            PERMISSION_EXTERNAL_STORAGE_CODE = 200;

    private static final String USERS_EXTRA = "USERS_EXTRA";

    @Inject
    PackageManager packageManager;

    private File file;

    public static GroupUsersFragment newInstance(List<User> users) {

        Bundle args = new Bundle();
        try {
            args.putString(USERS_EXTRA, LoganSquare.serialize(users, User.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        GroupUsersFragment fragment = new GroupUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected GroupUsersViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(GroupUsersViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_group_users;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        dataBinding.setAdapter(new ContactAdapter());
        viewModel.users.observe(this, users -> dataBinding.getAdapter().updateItems(users));
        viewModel.coverClickEvent.observe(this, aVoid -> {
            openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_camera, R.drawable.ic_media}, new int[]{R.string.sheet_photo, R.string.sheet_avatar_gallery}), viewModel.avatarSheetFragmentTag, REQUEST_AVATAR_CODE);
        });

        if (getArguments() != null)
            try {
                viewModel.users.setValue(LoganSquare.parseList(getArguments().getString(USERS_EXTRA), User.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_AVATAR_CODE:
                    switch (data.getIntExtra(Intent.EXTRA_TEXT, R.string.sheet_avatar_gallery)) {
                        case R.string.sheet_photo:
                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        PERMISSION_EXTERNAL_STORAGE_CODE);
                            else openCamera();
                            break;
                        case R.string.sheet_avatar_gallery:
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setDataAndType(
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                            startActivityForResult(intent, REQUEST_GALLERY_CODE);
                            break;
                    }
                    break;
                case REQUEST_PHOTO_CODE:
                    try {
                        Bitmap bitmap = BitmapHandler.handleSamplingAndRotationBitmap(getContext(), Uri.fromFile(file));
                        BitmapHandler.compress(bitmap, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    viewModel.setAvatar(Uri.fromFile(file));
                    viewModel.notifyChanged(BR.avatar);
                    break;
                case REQUEST_GALLERY_CODE:
                    if (data != null) {
                        Uri result = data.getData();
                        file = FileUtil.createTempFile(getContext(), "cover", ".jpeg");
                        if (FileUtil.copyFile(getContext(), result, file))
                            try {
                                Bitmap bitmap = BitmapHandler.handleSamplingAndRotationBitmap(getContext(), Uri.fromFile(file));
                                BitmapHandler.compress(bitmap, file);
                                viewModel.setAvatar(Uri.fromFile(file));
                                viewModel.notifyChanged(BR.avatar);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                    break;
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_check, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:
                viewModel.createGroup(file,  FileUtil.getMimeType(getActivity(), viewModel.getAvatar()), chatId -> {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_ID_EXTRA, chatId);
                    startActivity(intent);
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(packageManager) != null && (file = FileUtil.createTempFile(getContext(), "cover", ".jpeg")) != null) {
            Uri photoUri = FileProvider.getUriForFile(getContext(),
                    "ru.vital.daily.fileProvider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        startActivityForResult(intent, REQUEST_PHOTO_CODE);
    }
}
