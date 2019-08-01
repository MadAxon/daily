package ru.vital.daily.repository.data;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import ru.vital.daily.BR;

@Entity(tableName = "users")
@JsonObject
public class User extends BaseObservable {

    @PrimaryKey
    @JsonField
    private long id;

    @Nullable
    @JsonField
    private String uname, phone, email, firstname, middlename, lastname, type, status, sex, birthDate, website, about;

    @Nullable
    @JsonField
    private Date onlineAt, registerAt;

    @Nullable
    @JsonField
    private Double rating;

    @Nullable
    @JsonField
    private Media avatar;

    @JsonIgnore
    @Ignore
    private boolean selected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getPhone() {
        return phone;
    }

    public void setPhone(@Nullable String phone) {
        this.phone = phone;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
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
    public String getType() {
        return type;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    @Nullable
    public String getSex() {
        return sex;
    }

    public void setSex(@Nullable String sex) {
        this.sex = sex;
    }

    @Nullable
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(@Nullable String birthDate) {
        this.birthDate = birthDate;
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

    @Nullable
    public Date getOnlineAt() {
        return onlineAt;
    }

    public void setOnlineAt(@Nullable Date onlineAt) {
        this.onlineAt = onlineAt;
    }

    @Nullable
    public Date getRegisterAt() {
        return registerAt;
    }

    public void setRegisterAt(@Nullable Date registerAt) {
        this.registerAt = registerAt;
    }

    @Nullable
    public Double getRating() {
        return rating;
    }

    public void setRating(@Nullable Double rating) {
        this.rating = rating;
    }

    @Nullable
    public Media getAvatar() {
        return avatar;
    }

    public void setAvatar(@Nullable Media avatar) {
        this.avatar = avatar;
    }

    @Nullable
    public String getUname() {
        return uname;
    }

    public void setUname(@Nullable String uname) {
        this.uname = uname;
    }

    @Bindable
    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        notifyPropertyChanged(BR.selected);
    }
}
