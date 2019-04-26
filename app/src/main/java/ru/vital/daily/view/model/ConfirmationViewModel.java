package ru.vital.daily.view.model;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ru.vital.daily.listener.SingleLiveEvent;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.EmailRequest;
import ru.vital.daily.repository.api.request.PhoneRequest;
import ru.vital.daily.repository.api.response.ErrorResponse;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.util.DisposableProvider;

public class ConfirmationViewModel extends ViewModel {

    public final String registerFragmentTag = "registerFragmentTag";

    public String codeHash;

    public boolean isPhoneSignIn;

    public final PhoneRequest phoneRequest;

    public final EmailRequest emailRequest;

    private final KeyRepository keyRepository;

    private final UserRepository userRepository;

    private String code;

    private Disposable confirmationDisposable;

    @Inject
    public ConfirmationViewModel(PhoneRequest phoneRequest, EmailRequest emailRequest, KeyRepository keyRepository, UserRepository userRepository) {
        this.phoneRequest = phoneRequest;
        this.emailRequest = emailRequest;
        this.keyRepository = keyRepository;
        this.userRepository = userRepository;
    }

    public void signIn(Consumer<Key> onSuccess) {
        isLoading.set(true);
        if (confirmationDisposable != null && !compositeDisposable.isDisposed()) return;
        compositeDisposable.add(confirmationDisposable = DisposableProvider.getDisposableItem(isPhoneSignIn ? keyRepository.retrieveNewKey(phoneRequest) : keyRepository.retrieveNewKey(emailRequest), key -> {
            keyRepository.insertKey(key);
            onSuccess.accept(key);
        }, throwable -> {
            errorEvent.setValue(throwable);
            isLoading.set(false);
        }));
    }

    public void updateKeyUserId(Consumer<Void> onContinue) {
        compositeDisposable.add(confirmationDisposable = DisposableProvider.getDisposableItem(userRepository.getProfile(0), user -> {
            keyRepository.updateCurrentUserId(user.getId());
            onContinue.accept(null);
        }, throwable -> {
            onContinue.accept(null);
        }));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        if (isPhoneSignIn) phoneRequest.setCode(code);
        else emailRequest.setCode(code);
    }
}
