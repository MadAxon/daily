package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import ru.vital.daily.BR;

@JsonObject
public class ItemsRequest extends BaseObservable {

    @JsonField
    @Nullable
    private Integer pageIndex, pageSize;

    @JsonField
    @Nullable
    private String direction, orderBy, searchText;

    @JsonField
    @Nullable
    private Long subjectId, objectId;

    @Inject
    public ItemsRequest() {
    }

    @Nullable
    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(@Nullable Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Nullable
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(@Nullable Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Nullable
    public String getDirection() {
        return direction;
    }

    public void setDirection(@Nullable String direction) {
        this.direction = direction;
    }

    @Nullable
    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(@Nullable String orderBy) {
        this.orderBy = orderBy;
    }

    @Nullable
    @Bindable
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(@Nullable String searchText) {
        this.searchText = searchText;
        notifyPropertyChanged(BR.searchText);
    }

    @Nullable
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(@Nullable Long subjectId) {
        this.subjectId = subjectId;
    }

    @Nullable
    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(@Nullable Long objectId) {
        this.objectId = objectId;
    }
}
