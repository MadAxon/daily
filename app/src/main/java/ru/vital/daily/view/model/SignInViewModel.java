package ru.vital.daily.view.model;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.request.EmailRequest;
import ru.vital.daily.repository.api.request.PhoneRequest;
import ru.vital.daily.repository.model.CodeHashModel;
import ru.vital.daily.util.DisposableProvider;

public class SignInViewModel extends ViewModel {

    public final String confirmationFragmentTag = "confirmationFragmentTag";

    public boolean isPhoneSignIn;

    public final PhoneRequest phoneRequest;

    public final EmailRequest emailRequest;

    private final Api api;

    private Disposable requestCodeDisposable;

    @Inject
    public SignInViewModel(PhoneRequest phoneRequest, EmailRequest emailRequest, Api api) {
        this.phoneRequest = phoneRequest;
        this.emailRequest = emailRequest;
        this.api = api;
    }

    public void requestCode(Consumer<CodeHashModel> onSuccess) {
        if (requestCodeDisposable != null && !requestCodeDisposable.isDisposed()) {
            return;
        }
        isLoading.set(true);
        compositeDisposable.add(requestCodeDisposable = DisposableProvider.getDisposableItem(isPhoneSignIn ? api.requestSms(phoneRequest) : api.requestEmail(emailRequest), codeHashModel -> {
            isLoading.set(false);
            onSuccess.accept(codeHashModel);
        }, throwable -> {
            errorEvent.setValue(throwable);
            isLoading.set(false);
        }));
    }

}
