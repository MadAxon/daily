package ru.vital.daily.view.model;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ru.vital.daily.repository.KeyRepository;
import ru.vital.daily.repository.UserRepository;
import ru.vital.daily.repository.api.DailySocket;
import ru.vital.daily.repository.api.request.EmailRequest;
import ru.vital.daily.repository.api.request.PhoneRequest;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;
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
            keyRepository.saveKey(key);
            onSuccess.accept(key);
            isLoading.set(false);
        }, throwable -> {
            errorEvent.setValue(throwable);
            isLoading.set(false);
        }));
    }

    public void updateKeyUserId(Consumer<User> onContinue) {
        compositeDisposable.add(confirmationDisposable = DisposableProvider.getDisposableItem(userRepository.getProfile(0), user -> {
            userRepository.saveUser(user);
            keyRepository.updateCurrentUserId(user.getId());
            onContinue.accept(user);
        }, throwable -> {
            onContinue.accept(new User());
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
