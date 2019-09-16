package ru.vital.daily.util;

import androidx.collection.LongSparseArray;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vital.daily.repository.data.Message;

@Singleton
public class ChatData {

    public final LongSparseArray<List<Message>> items = new LongSparseArray<>();

    @Inject
    public ChatData() {

    }

}
