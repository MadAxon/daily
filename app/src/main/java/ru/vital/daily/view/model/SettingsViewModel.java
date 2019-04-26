package ru.vital.daily.view.model;

import android.app.Application;

import androidx.annotation.NonNull;
import ru.vital.daily.listener.SingleLiveEvent;

public class SettingsViewModel extends ViewModel {

    public final String settingsNotificationFragmentTag = "settingsNotificationFragmentTag",
                    settingsFragmentTag = "settingsFragmentTag",
                    settingsPersonalFragmentTag = "settingsPersonalFragmentTag";

    public SingleLiveEvent<Void> languageClickedEvent = new SingleLiveEvent<>(),
                                notificationClickedEvent = new SingleLiveEvent<>(),
                                phoneClickedEvent = new SingleLiveEvent<>(),
                                loginClickedEvent = new SingleLiveEvent<>(),
                                descriptionClickedEvent = new SingleLiveEvent<>(),
                                emailClickedEvent = new SingleLiveEvent<>(),
                                likedClickedEvent = new SingleLiveEvent<>(),
                                historyClickedEvent = new SingleLiveEvent<>(),
                                originalClickedEvent = new SingleLiveEvent<>(),
                                privacyClickedEvent = new SingleLiveEvent<>(),
                                faqClickedEvent = new SingleLiveEvent<>(),
                                policyClickedEvent = new SingleLiveEvent<>(),
                                conditionClickedEvent = new SingleLiveEvent<>();

    public String versionName;

    public final String fragmentPhoneTag = "fragmentPhoneTag",
            fragmentLoginTag = "fragmentLoginTag",
            fragmentDescriptionTag = "fragmentDescriptionTag",
            fragmentEmailTag = "fragmentEmailTag",
            fragmentHistoryTag = "fragmentHistoryTag";

    public void onConditionClicked() {
        conditionClickedEvent.call();
    }

    public void onPolicyClicked() {
        policyClickedEvent.call();
    }

    public void onFaqClicked() {
        faqClickedEvent.call();
    }

    public void onPrivacyClicked() {
        privacyClickedEvent.call();
    }

    public void onOriginalClicked() {
        originalClickedEvent.call();
    }

    public void onHistoryClicked() {
        historyClickedEvent.call();
    }

    public void onLikedClicked() {
        likedClickedEvent.call();
    }

    public void onEmailClicked() {
        emailClickedEvent.call();
    }

    public void onDescriptionClicked() {
        descriptionClickedEvent.call();
    }

    public void onLoginClicked() {
        loginClickedEvent.call();
    }

    public void onPhoneClicked() {
        phoneClickedEvent.call();
    }

    public void onLanguageClicked() {
        languageClickedEvent.call();
    }

    public void onNotificationClicked() {
        notificationClickedEvent.call();
    }
}
