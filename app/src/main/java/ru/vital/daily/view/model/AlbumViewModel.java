package ru.vital.daily.view.model;

import android.util.SparseArray;

import java.util.List;

import androidx.collection.LongSparseArray;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import javax.inject.Inject;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.util.SelectedMedias;

public class AlbumViewModel extends ViewModel {

    public final LongSparseArray<List<Long>> mediaSparseArray = new LongSparseArray<>();

    public final ObservableField<String> title = new ObservableField<>();

    public final SingleLiveEvent<Void> sendClickEvent = new SingleLiveEvent<>();

    public final LongSparseArray<Media> medias = new LongSparseArray<>();

    private final SelectedMedias selectedMedias;

    @Inject
    public AlbumViewModel(SelectedMedias selectedMedias) {
        this.selectedMedias = selectedMedias;
    }

    public void onSendClicked() {
        sendClickEvent.call();
    }

    public SelectedMedias getSelectedMedias() {
        return selectedMedias;
    }
}
