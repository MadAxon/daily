package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.enums.SettingsPersonalType;
import ru.vital.daily.listener.SingleLiveEvent;

public class SettingsPersonalViewModel extends ViewModel {

    public int title;

    public SettingsPersonalType loginType = SettingsPersonalType.login,
                            phoneType = SettingsPersonalType.phone,
                            emailType = SettingsPersonalType.email,
                            description = SettingsPersonalType.description,
                            currentType;

    SingleLiveEvent<Void> countryClickedEvent = new SingleLiveEvent<>();

    public void onCountryClick() {
        countryClickedEvent.call();
    }
}
