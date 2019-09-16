package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import javax.inject.Inject;

@JsonObject
public class JoinRequest extends IdRequest {

    @JsonField
    private long[] memberIds;

    @Inject
    public JoinRequest() {
    }

    public long[] getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(long[] memberIds) {
        this.memberIds = memberIds;
    }
}
