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

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.databinding.FragmentChannelCreateBinding;
import ru.vital.daily.fragment.sheet.SimpleSheetFragment;
import ru.vital.daily.util.BitmapHandler;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.view.model.ChannelCreateViewModel;

public class ChannelCreateFragment extends BaseFragment<ChannelCreateViewModel, FragmentChannelCreateBinding> {

    private final int
            REQUEST_COVER_CODE = 101,
            REQUEST_PHOTO_CODE = 102,
            REQUEST_GALLERY_CODE = 103,
            PERMISSION_EXTERNAL_STORAGE_CODE = 200;

    @Inject
    PackageManager packageManager;

    private File file;

    @Override
    protected ChannelCreateViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(ChannelCreateViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_channel_create;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar(dataBinding.toolbar, true);

        viewModel.coverClickEvent.observe(this, aVoid -> {
            openSheetFragment(SimpleSheetFragment.newInstance(new int[]{R.drawable.ic_camera, R.drawable.ic_media}, new int[]{R.string.sheet_avatar_photo, R.string.sheet_avatar_gallery}), viewModel.avatarSheetFragmentTag, REQUEST_COVER_CODE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_COVER_CODE:
                    switch (data.getIntExtra(Intent.EXTRA_TEXT, R.string.sheet_avatar_gallery)) {
                        case R.string.sheet_avatar_photo:
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
                        if (FileUtil.copyImageFile(getContext(), result, file))
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera();
                else
                    viewModel.errorEvent.setValue(new Throwable(getString(R.string.permission_camera_denied)));
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_check, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_check:
                hideSoftKeyboard();
                if (viewModel.getChatSaveModel().getName() != null && !viewModel.getChatSaveModel().getName().isEmpty())
                    openFragment(ChannelSettingsFragment.newInstance(viewModel.getChatSaveModel(), viewModel.getAvatar() != null ? viewModel.getAvatar().toString() : null), viewModel.groupSettingsFragmentTag);
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
