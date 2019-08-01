package ru.vital.daily.repository.api;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final ProgressListener progressListener;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        ProgressSink countingSink = new ProgressSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);

        requestBody.writeTo(bufferedSink);

        bufferedSink.flush();
    }

    final class ProgressSink extends ForwardingSink {

        private long bytesWritten = 0;

        ProgressSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            progressListener.onUpdated(bytesWritten, contentLength(), bytesWritten == contentLength());
        }

    }

}
