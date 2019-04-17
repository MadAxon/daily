package ru.vital.daily.enums;

import ru.vital.daily.R;

public enum SettingsPersonalType {
    login(R.string.settings_login),
    phone(R.string.settings_phone),
    description(R.string.settings_profile_description),
    email(R.string.settings_email);

    public int stringId;

    SettingsPersonalType(int stringId) {
        this.stringId = stringId;
    }
}
