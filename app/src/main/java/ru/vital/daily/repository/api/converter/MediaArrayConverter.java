package ru.vital.daily.repository.api.converter;

import androidx.collection.LongSparseArray;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.vital.daily.repository.data.Media;

public class MediaArrayConverter implements TypeConverter<LongSparseArray<Media>> {

    @Override
    public LongSparseArray<Media> parse(JsonParser jsonParser) throws IOException {
            List<Media> medias = LoganSquare.mapperFor(Media.class).parseList(jsonParser);
            LongSparseArray<Media> mediasSparseArray = new LongSparseArray<>();
            for (Media media : medias)
                mediasSparseArray.put(media.getId(), media);
            return mediasSparseArray;
    }

    @Override
    public void serialize(LongSparseArray<Media> object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
        final int size = object != null ? object.size() : 0;
        List<Media> medias = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            medias.add(object.valueAt(i));
        if (fieldName != null) {
            jsonGenerator.writeStringField(fieldName, LoganSquare.serialize(medias, Media.class));
        } else {
            jsonGenerator.writeString(LoganSquare.serialize(medias, Media.class));
        }
    }
}
