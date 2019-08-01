package ru.vital.daily.repository.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import ru.vital.daily.repository.api.request.IdRequest;

@JsonObject
public class MediaEditModel extends IdRequest {

    @JsonField
    private String name, description;

    public MediaEditModel() {
    }

    public MediaEditModel(Long id, String description) {
        super(id);
        this.description = description;
    }

    public MediaEditModel(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
