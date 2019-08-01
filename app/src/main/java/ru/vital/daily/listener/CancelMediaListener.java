package ru.vital.daily.listener;

import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;

public interface CancelMediaListener {

    void cancel(Message message, Media media);

}
