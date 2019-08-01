package ru.vital.daily.repository.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ProgressApi {

    @GET
    @Streaming
    Observable<ResponseBody> download(@Url String url);

}
