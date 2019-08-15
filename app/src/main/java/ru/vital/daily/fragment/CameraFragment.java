package ru.vital.daily.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bluelinelabs.logansquare.LoganSquare;

import javax.inject.Inject;

import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.MediaEditorActivity;
import ru.vital.daily.adapter.MediaAdapter;
import ru.vital.daily.databinding.FragmentCameraBinding;
import ru.vital.daily.fragment.sheet.ChatSheetFragment;
import ru.vital.daily.fragment.sheet.SimpleSheetFragment;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.BitmapHandler;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.view.model.CameraViewModel;
import ru.vital.daily.view.model.ChatSheetViewModel;

public class CameraFragment extends BaseFragment<CameraViewModel, FragmentCameraBinding> {

    private final int REQUEST_MEDIA_CODE = 101,
            REQUEST_PHOTO_CODE = 102,
            REQUEST_VIDEO_CODE = 103,
            REQUEST_GALLERY_CODE = 104,
            PERMISSION_WRITE_EXTERNAL_STORAGE_CODE = 200,
            PERMISSION_READ_EXTERNAL_STORAGE_CODE = 201;

    private ChatSheetViewModel chatSheetViewModel;

    private File file;

    @Inject
    PackageManager packageManager;

    @Override
    protected CameraViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(CameraViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_camera;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chatSheetViewModel = ViewModelProviders.of(getParentFragment()).get(ChatSheetViewModel.class);
        chatSheetViewModel.cameraSheetClickEvent.observe(this, integer -> {
            handleCameraSheetClick(integer);
        });

        viewModel.getSelectedMedias().clear();

        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        dataBinding.setAdapter(new MediaAdapter(false, 3));
        dataBinding.getAdapter().clickEvent.observe(this, id -> {
            if (id == 0L) {
                SimpleSheetFragment sheetDialogFragment = SimpleSheetFragment.newInstance(new int[]{R.string.sheet_photo, R.string.sheet_video});
                if (getParentFragment().getChildFragmentManager().findFragmentByTag(viewModel.cameraSheetFragmentTag) == null) {
                    sheetDialogFragment.setTargetFragment(getParentFragment(), REQUEST_MEDIA_CODE);
                    sheetDialogFragment.show(getActivity().getSupportFragmentManager(), viewModel.cameraSheetFragmentTag);
                }
            } else {
                Intent intent = new Intent(getContext(), MediaEditorActivity.class);
                intent.putExtra(MediaEditorActivity.MEDIA_CURRENT_EXTRA, dataBinding.getAdapter().getItems().indexOf(id) - 1);
                try {
                    intent.putExtra(MediaEditorActivity.MEDIA_LIST_EXTRA, LoganSquare.serialize(dataBinding.getAdapter().getItems().subList(1, dataBinding.getAdapter().getItems().size() - 1), Long.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                startActivityForResult(intent, REQUEST_GALLERY_CODE);
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE_CODE);
        else loadMedias();

        dataBinding.getAdapter().checkClickEvent.observe(this, media -> {
            viewModel.getSelectedMedias().checkMedia(media);
            if (viewModel.getSelectedMedias().getSelected().size() > 0)
                dataBinding.floatingActionButton.show();
            else dataBinding.floatingActionButton.hide();
        });
        viewModel.sendClickEvent.observe(this, aVoid -> send());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel.getSelectedMedias().getSelected().size() > 0)
            dataBinding.floatingActionButton.show();
        else dataBinding.floatingActionButton.hide();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("my_logs", "onRequestPermissionsResult()");
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    handleCameraSheetClick(chatSheetViewModel.cameraSheetClickEvent.getValue());
                else
                    viewModel.errorEvent.setValue(new Throwable(getString(R.string.permission_camera_denied)));
                break;
            case PERMISSION_READ_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    loadMedias();
                else
                    viewModel.errorEvent.setValue(new Throwable(getString(R.string.permission_media_denied)));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri result = data != null ? data.getData() : null;
            switch (requestCode) {
                case REQUEST_PHOTO_CODE:
                    try {
                        Bitmap bitmap = BitmapHandler.handleSamplingAndRotationBitmap(getContext(), Uri.fromFile(file));
                        BitmapHandler.compress(bitmap, file);
                        String mimeType = FileUtil.getMimeType(getContext(), Uri.fromFile(file));

                        Media media = new Media(System.currentTimeMillis() * -1, file.getName(), FileUtil.getFileType(mimeType), new MediaModel(file.getPath(), mimeType));
                        media.setSelected(true);
                        dataBinding.getAdapter().addCapturedMedia(media);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_VIDEO_CODE:
                    String mimeType = FileUtil.getMimeType(getContext(), result);
                    File file = FileUtil.createTempFile(getContext(), "video", ".3gpp");
                    FileUtil.copyFile(getContext(), result, file);
                    Media media = new Media(System.currentTimeMillis() * -1, file.getName(), FileUtil.getFileType(mimeType), new MediaModel(file.getPath(), mimeType));
                    media.setSelected(true);
                    dataBinding.getAdapter().addCapturedMedia(media);
                    break;
                case REQUEST_GALLERY_CODE:
                    send();
                    break;
            }
        }
    }

    private void loadMedias() {
        Log.i("my_logs", "loadMedias()");
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                MediaStore.Files.getContentUri("external"),
                new String[]{
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                },
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        final Cursor cursor = cursorLoader.loadInBackground();
        if (cursor != null) {

            Log.i("my_logs", "loadMedias() " + cursor.getCount());
            dataBinding.getAdapter().setSelectedMedias(viewModel.getSelectedMedias().getMedias());
            dataBinding.getAdapter().addNull();
            while (cursor.moveToNext()) {
                String type = cursor.getString(3);
                Media media = new Media(cursor.getLong(0) * -1, cursor.getString(2), FileUtil.getFileType(type), new MediaModel(cursor.getString(1), type));
                dataBinding.getAdapter().addMedia(media);
            }
            cursor.close();
            dataBinding.getAdapter().notifyInserted();
        }
    }

    private void openPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(packageManager) != null && (file = FileUtil.createTempFile(getContext(), "avatar", ".jpeg")) != null) {
            Uri photoUri = FileProvider.getUriForFile(getContext(),
                    "ru.vital.daily.fileProvider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        startActivityForResult(intent, REQUEST_PHOTO_CODE);
    }

    private void openVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CODE);
        }
    }

    private void handleCameraSheetClick(Integer stringId) {
        switch (stringId) {
            case R.string.sheet_photo:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE_CODE);
                else openPhoto();
                break;
            case R.string.sheet_video:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE_CODE);
                else openVideo();
                break;
        }
    }

    private void send() {
        if (getParentFragment() != null && getParentFragment() instanceof ChatSheetFragment)
            ((ChatSheetFragment) getParentFragment()).sendSelectedMedias(viewModel.getSelectedMedias().getSelected());
    }
}
