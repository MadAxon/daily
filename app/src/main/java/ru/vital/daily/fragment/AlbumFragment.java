package ru.vital.daily.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bluelinelabs.logansquare.LoganSquare;

import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.activity.MediaEditorActivity;
import ru.vital.daily.adapter.MediaAdapter;
import ru.vital.daily.databinding.FragmentAlbumBinding;
import ru.vital.daily.fragment.sheet.ChatSheetFragment;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.util.GridItemDoubleDecoration;
import ru.vital.daily.view.model.AlbumViewModel;

public class AlbumFragment extends BaseFragment<AlbumViewModel, FragmentAlbumBinding> {

    private final int REQUEST_GALLERY_CODE = 104;

    @Override
    protected AlbumViewModel onCreateViewModel() {
        return ViewModelProviders.of(this, viewModelFactory).get(AlbumViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_album;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar(dataBinding.toolbar, true);

        dataBinding.toolbar.setNavigationOnClickListener(v -> {
            viewModel.title.set(null);
            dataBinding.getAdapter().clearAdapter();
            dataBinding.getAdapter().setCurrentViewType(4);
            dataBinding.getAdapter().setSelectedMedias(viewModel.medias);
            dataBinding.getAdapter().updateMedias(viewModel.mediaSparseArray.get(0));
        });

        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dataBinding.recyclerView.addItemDecoration(new GridItemDoubleDecoration());
        dataBinding.setAdapter(new MediaAdapter(false, 4));

        dataBinding.getAdapter().clickEvent.observe(this, id -> {
            Media album = viewModel.medias.get(id);
            if (album != null && album.getName() != null) {
                viewModel.title.set(album.getName());
                dataBinding.getAdapter().clearAdapter();
                dataBinding.getAdapter().setCurrentViewType(0);
                dataBinding.getAdapter().setSelectedMedias(viewModel.getSelectedMedias().getMedias());
                dataBinding.getAdapter().updateMedias(viewModel.mediaSparseArray.get(id));
            } else {
                Intent intent = new Intent(getContext(), MediaEditorActivity.class);
                intent.putExtra(MediaEditorActivity.MEDIA_CURRENT_EXTRA, dataBinding.getAdapter().getItems().indexOf(id));
                try {
                    intent.putExtra(MediaEditorActivity.MEDIA_LIST_EXTRA, LoganSquare.serialize(dataBinding.getAdapter().getItems(), Long.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                startActivityForResult(intent, REQUEST_GALLERY_CODE);
            }
        });

        viewModel.mediaSparseArray.put(0, new ArrayList<>());
        loadAllMedias();
        loadAllVideos();

        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        "DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_ID,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                }, null, null, null);
        if (cursor != null) {

            dataBinding.getAdapter().setSelectedMedias(viewModel.medias);
            //dataBinding.getAdapter().setSelectedMedias(viewModel.getSelectedMedias().getMedias());
            while (cursor.moveToNext())
                loadAlbum(cursor);
            cursor.close();
        }

        dataBinding.getAdapter().updateMedias(viewModel.mediaSparseArray.get(0));
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_GALLERY_CODE:
                    send();
                    break;
            }
    }

    private void loadAlbum(Cursor cursor) {
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                MediaStore.Files.getContentUri("external"),
                new String[]{
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                },
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")"
                        + " AND " + MediaStore.Images.ImageColumns.BUCKET_ID + "=" + cursor.getString(0),
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        Cursor mediaCursor = cursorLoader.loadInBackground();
        if (mediaCursor != null && mediaCursor.getCount() > 0 && mediaCursor.moveToNext()) {
            String url = mediaCursor.getString(0);
            String type = mediaCursor.getString(1);
            Media album = new Media(cursor.getLong(0), cursor.getString(1), null, new MediaModel(url, type, mediaCursor.getCount()));
            viewModel.mediaSparseArray.get(0).add(album.getId());
            viewModel.medias.put(album.getId(), album);

            List<Long> albumMedias = new ArrayList<>();
            albumMedias.add(mediaCursor.getLong(2) * -1);
            while (mediaCursor.moveToNext()) {
                albumMedias.add(mediaCursor.getLong(2) * -1);
            }
            viewModel.mediaSparseArray.put(cursor.getLong(0), albumMedias);
            /*List<Media> albumMedias = new ArrayList<>();
            albumMedias.add(new Media(mediaCursor.getLong(2) * -1, cursor.getString(1), FileUtil.getFileType(type), new MediaModel(url, type)));
            while (mediaCursor.moveToNext()) {
                type = mediaCursor.getString(1);
                albumMedias.add(new Media(mediaCursor.getLong(2) * -1, mediaCursor.getString(3), FileUtil.getFileType(type), new MediaModel(mediaCursor.getString(0), type)));
            }
            viewModel.mediaSparseArray.put(cursor.getLong(0), albumMedias);*/
        }
    }

    private void loadAllMedias() {
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                MediaStore.Files.getContentUri("external"),
                new String[] {
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                },
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        Cursor mediaCursor = cursorLoader.loadInBackground();
        if (mediaCursor != null && mediaCursor.getCount() > 0 && mediaCursor.moveToNext()) {
            String url = mediaCursor.getString(0);
            String type = mediaCursor.getString(1);
            Media album = new Media(-1, getString(R.string.sheet_album_all_medias), null, new MediaModel(url, type, mediaCursor.getCount()));
            viewModel.mediaSparseArray.get(0).add(album.getId());
            viewModel.medias.put(album.getId(), album);

            List<Long> allMedias = new ArrayList<>();
            allMedias.add(mediaCursor.getLong(2) * -1);
            while (mediaCursor.moveToNext()) {
                allMedias.add(mediaCursor.getLong(2) * -1);
            }
            viewModel.mediaSparseArray.put(-1, allMedias);
            /*List<Media> allMedias = new ArrayList<>();
            allMedias.add(new Media(mediaCursor.getLong(2) * -1, mediaCursor.getString(3), FileUtil.getFileType(type), new MediaModel(url, type)));
            while (mediaCursor.moveToNext()) {
                type = mediaCursor.getString(1);
                allMedias.add(new Media(mediaCursor.getLong(2) * -1, mediaCursor.getString(3) , FileUtil.getFileType(type), new MediaModel(mediaCursor.getString(0), type)));
            }
            viewModel.mediaSparseArray.put(-1, allMedias);*/
        }
    }

    private void loadAllVideos() {
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                MediaStore.Files.getContentUri("external"),
                new String[] {
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                },
                MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );
        Cursor mediaCursor = cursorLoader.loadInBackground();
        if (mediaCursor != null && mediaCursor.getCount() > 0 && mediaCursor.moveToNext()) {
            String url = mediaCursor.getString(0);
            String type = mediaCursor.getString(1);
            Media album = new Media(-2, getString(R.string.sheet_album_all_videos), null, new MediaModel(url, type, mediaCursor.getCount()));
            viewModel.mediaSparseArray.get(0).add(album.getId());
            viewModel.medias.put(album.getId(), album);

            List<Long> allMedias = new ArrayList<>();
            allMedias.add(mediaCursor.getLong(2) * -1);
            while (mediaCursor.moveToNext()) {
                allMedias.add(mediaCursor.getLong(2) * -1);
            }
            viewModel.mediaSparseArray.put(-2, allMedias);

            /*List<Media> allMedias = new ArrayList<>();
            allMedias.add(new Media(mediaCursor.getLong(2) * -1, mediaCursor.getString(3), FileUtil.getFileType(type), new MediaModel(url, type)));
            while (mediaCursor.moveToNext()) {
                type = mediaCursor.getString(1);
                allMedias.add(new Media(mediaCursor.getLong(2) * -1, mediaCursor.getString(3), FileUtil.getFileType(type), new MediaModel(mediaCursor.getString(0), type)));
            }
            viewModel.mediaSparseArray.put(-2, allMedias);*/
        }
    }

    private void send() {
        if (getParentFragment() != null && getParentFragment() instanceof ChatSheetFragment)
            ((ChatSheetFragment) getParentFragment()).sendSelectedMedias(viewModel.getSelectedMedias().getSelected());
    }
}
