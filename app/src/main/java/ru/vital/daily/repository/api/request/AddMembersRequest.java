package ru.vital.daily.repository.api.request;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import javax.inject.Inject;

@JsonObject
public class AddMembersRequest extends IdRequest {

    @JsonField
    private long[] memberIds;

    @Inject
    public AddMembersRequest() {
    }

    public long[] getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(long[] memberIds) {
        this.memberIds = memberIds;
    }
}
