package ru.vital.daily.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import ru.vital.daily.BR;
import ru.vital.daily.R;
import ru.vital.daily.adapter.MediaAdapter;
import ru.vital.daily.databinding.FragmentMediaBinding;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.util.GridItemDoubleDecoration;
import ru.vital.daily.view.model.MediaViewModel;

public class MediaFragment extends BaseFragment<MediaViewModel, FragmentMediaBinding> {

    @Override
    protected MediaViewModel onCreateViewModel() {
        return ViewModelProviders.of(this).get(MediaViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_media;
    }

    @Override
    protected int getVariable() {
        return BR.viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dataBinding.recyclerView.addItemDecoration(new GridItemDoubleDecoration());
        dataBinding.setAdapter(new MediaAdapter(false, 0));

        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                MediaStore.Files.getContentUri("external"),
                new String[] {
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.DATE_ADDED,
                        MediaStore.Files.FileColumns.MEDIA_TYPE,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns.TITLE,
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
            List<Media> items = new ArrayList<>();
            items.add(null);
            while (cursor.moveToNext()) {
                String type = cursor.getString(4);
                items.add(new Media(cursor.getLong(0), FileUtil.getFileType(type), new MediaModel(cursor.getString(1), type)));
            }
            cursor.close();
            //dataBinding.getAdapter().updateMedias(items);
        }
    }
}
