package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class IdsRequest {

    @JsonField
    private long[] ids;

    public IdsRequest() {
    }

    public IdsRequest(long[] ids) {
        this.ids = ids;
    }

    public long[] getIds() {
        return ids;
    }

    public void setIds(long[] ids) {
        this.ids = ids;
    }
}
