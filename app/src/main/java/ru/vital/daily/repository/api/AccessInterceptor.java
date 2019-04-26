package ru.vital.daily.repository.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import ru.vital.daily.repository.data.Key;

public class AccessInterceptor implements Interceptor {

    private String accessKey;

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() == null || !request.body().contentType().toString().contains("multipart/form-data")) {
            Request.Builder requestBuilder = request.newBuilder();
            String postBody = bodyToString(request.body());
            if (accessKey != null)
                try {
                    JSONObject jsonObject = new JSONObject(postBody);
                    jsonObject.put("accessKey", accessKey);
                    postBody = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            request = requestBuilder
                    .post(RequestBody.create(request.body().contentType(), postBody))
                    .build();
            Log.i("my_logs", request.body().contentType() + " | " + request.url().toString() + " | " + postBody);
        }
        return chain.proceed(request);
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    private String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            if (request != null)
                request.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return e.getLocalizedMessage();
        }
    }
}
