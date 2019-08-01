package ru.vital.daily.view.model;

import javax.inject.Inject;

import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.util.SelectedMedias;

public class CameraViewModel extends ViewModel {

    public final String cameraSheetFragmentTag = "cameraSheetFragmentTag";

    public SingleLiveEvent<Void> sendClickEvent = new SingleLiveEvent<>();

    private final SelectedMedias selectedMedias;

    @Inject
    public CameraViewModel(SelectedMedias selectedMedias) {
        this.selectedMedias = selectedMedias;
    }

    public void onSendClicked() {
        sendClickEvent.call();
    }

    public SelectedMedias getSelectedMedias() {
        return selectedMedias;
    }

}
