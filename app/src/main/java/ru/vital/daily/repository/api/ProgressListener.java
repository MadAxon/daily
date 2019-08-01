package ru.vital.daily.repository.api;

public interface ProgressListener {

    void onUpdated(long bytes, long contentLength, boolean done);

}
