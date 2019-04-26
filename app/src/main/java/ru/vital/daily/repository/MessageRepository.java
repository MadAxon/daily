package ru.vital.daily.repository;

import javax.inject.Singleton;

import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.db.MessageDao;

@Singleton
public class MessageRepository {

    private final Api api;

    private final MessageDao messageDao;

    public MessageRepository(Api api, MessageDao messageDao) {
        this.api = api;
        this.messageDao = messageDao;
    }
}
