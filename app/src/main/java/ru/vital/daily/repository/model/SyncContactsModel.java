package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import javax.inject.Inject;

@JsonObject
public class SyncContactsModel {

    @JsonField
    private List<String> phones;

    @Inject
    public SyncContactsModel() {
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }
}
