package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import javax.inject.Inject;

@JsonObject
public class PhoneRequest extends CodeRequest {

    @JsonField
    private String phone;

    @Inject
    public PhoneRequest() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
