package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import javax.inject.Inject;

import androidx.annotation.Nullable;

@JsonObject
public class ProfileSaveModel {

    @JsonField
    @Nullable
    private Long avatarId, backgroundId, cityId;

    @JsonField
    @Nullable
    private String uname, firstname, middlename, lastname, sex, website, about;

    @Inject
    public ProfileSaveModel() {
    }

    @Nullable
    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(@Nullable Long avatarId) {
        this.avatarId = avatarId;
    }

    @Nullable
    public Long getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(@Nullable Long backgroundId) {
        this.backgroundId = backgroundId;
    }

    @Nullable
    public Long getCityId() {
        return cityId;
    }

    public void setCityId(@Nullable Long cityId) {
        this.cityId = cityId;
    }

    @Nullable
    public String getUname() {
        return uname;
    }

    public void setUname(@Nullable String uname) {
        this.uname = uname;
    }

    @Nullable
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(@Nullable String firstname) {
        this.firstname = firstname;
    }

    @Nullable
    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(@Nullable String middlename) {
        this.middlename = middlename;
    }

    @Nullable
    public String getLastname() {
        return lastname;
    }

    public void setLastname(@Nullable String lastname) {
        this.lastname = lastname;
    }

    @Nullable
    public String getSex() {
        return sex;
    }

    public void setSex(@Nullable String sex) {
        this.sex = sex;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    public void setWebsite(@Nullable String website) {
        this.website = website;
    }

    @Nullable
    public String getAbout() {
        return about;
    }

    public void setAbout(@Nullable String about) {
        this.about = about;
    }
}
