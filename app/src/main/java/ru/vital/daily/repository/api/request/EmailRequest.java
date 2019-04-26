package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import javax.inject.Inject;

@JsonObject
public class EmailRequest extends CodeRequest {

    @JsonField
    private String email;

    @Inject
    public EmailRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
