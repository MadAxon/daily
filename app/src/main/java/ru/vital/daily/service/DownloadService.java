package ru.vital.daily.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;

import com.bluelinelabs.logansquare.LoganSquare;
import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.repository.MessageRepository;
import ru.vital.daily.repository.api.ProgressApi;
import ru.vital.daily.repository.api.ProgressInterceptor;
import ru.vital.daily.repository.api.response.handler.ItemResponseHandler;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.MediaModel;
import ru.vital.daily.util.DisposableProvider;
import ru.vital.daily.util.FileUtil;
import ru.vital.daily.util.MediaProgressHelper;

import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_CANCEL;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_START;
import static ru.vital.daily.enums.Operation.ACTION_MEDIA_DOWNLOAD_SUCCESS;

public class DownloadService extends JobIntentService {

    public static final String MESSAGE_EXTRA = "MESSAGE_EXTRA";

    @Inject
    MessageRepository messageRepository;

    @Inject
    MediaProgressHelper mediaProgressHelper;

    private final Subject<Float> progressSubject = PublishSubject.create();

    private long mediaId;

    private Disposable downloadDisposable, progressDisposable;

    private boolean cancelled;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, DownloadService.class, 1905, intent);
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            long cancelledMediaId = intent.getLongExtra(ChatActivity.MEDIA_ID_EXTRA, 0);
            switch (intent.getAction()) {
                case ACTION_MEDIA_DOWNLOAD_CANCEL:
                    if (mediaId == cancelledMediaId) {
                        downloadDisposable.dispose();
                        cancelled = true;
                        Log.i("my_logs", "compositeDisposable.clear()");
                    } else {
                        mediaProgressHelper.remove(cancelledMediaId);
                        Log.i("my_logs", "mediaProgressHelper.remove()");
                    }
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i("my_logs", "next download media");
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_MEDIA_DOWNLOAD_START:
                    Subject<Message> messageSubject = PublishSubject.create();
                    DisposableProvider.getDisposableItem(messageRepository.getMessage(intent.getLongExtra(ChatActivity.MESSAGE_ID_EXTRA, 0), intent.getLongExtra(ChatActivity.CHAT_ID_EXTRA, 0), false), message -> {
                        messageSubject.onNext(message);
                    }, throwable -> {
                        messageSubject.onNext(new Message());
                    });
                    Message message = messageSubject.blockingFirst();
                    messageSubject.onComplete();
                    /*if (cancelled) cancelled = false;
                    else*/ downloadMedia(message, intent.getLongExtra(ChatActivity.MEDIA_ID_EXTRA, 0));
                    break;
            }
        }
    }

    private void downloadMedia(Message message, long mediaId) {
        /*if (mediaProgressHelper.getMedia(media.getId()) != null)
            return;*/
        //mediaProgressHelper.putMedia(media);
        Log.i("my_logs", "mediasLength " + message.getMedias().size());
        this.mediaId = mediaId;
        Media media = mediaProgressHelper.getMedia(mediaId);
        if (media == null)
            return;
        //sendBroadcast(progressIntent);

        if (progressDisposable == null)
            progressDisposable = progressSubject.throttleLast(1, TimeUnit.SECONDS).subscribe(progress -> {
                Media progressMedia = mediaProgressHelper.getMedia(mediaId);
                if (progressMedia != null)
                    progressMedia.setProgress(progress);
            });

        media.setHasIconForProgress(false);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ProgressInterceptor((bytes, contentLength, done) -> {
                    //progressIntent.putExtra(ChatActivity.MEDIA_PROGRESS_EXTRA, done ? 1f : (float) bytes / contentLength);
                    float current = done ? 1f : (float) bytes / contentLength;
                    progressSubject.onNext(current);
                    /*if (progress + 0.05f <= current) {
                        progress = progress + current;
                        mediaProgressHelper.getMedia(mediaId).setProgress(progress);
                    }*/
                    //sendBroadcast(progressIntent);
                }))
                .build();
        ProgressApi progressApi = new Retrofit.Builder()
                .addConverterFactory(LoganSquareConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://dev.daily1.ru/api/")
                .client(okHttpClient)
                .build()
                .create(ProgressApi.class);

        MediaModel mediaModel = media.getFiles().get(0);

        if (!FileUtil.exists(mediaModel.getUrl())) {
            File file = FileUtil.createTempFile(this, media.getType(), media.getName());
            Subject<String> subject = PublishSubject.create();
            downloadDisposable = progressApi
                    .download(mediaModel.getUrl())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(ResponseBody::byteStream)
                    .observeOn(Schedulers.computation())
                    .doOnDispose(() -> subject.onNext(""))
                    .doOnNext(inputStream -> FileUtil.writeToFile(inputStream, file))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(inputStream -> {
                        subject.onNext(file.getPath());
                    }, throwable -> {
                        subject.onNext("");
                    });
            String url = subject.blockingFirst();
            subject.onComplete();
            if (url.isEmpty()) {
                //medias.remove(media.getId());
                Log.i("my_logs", "url is empty in service");
                Media downloadedMedia = mediaProgressHelper.getMedia(mediaId);
                if (downloadedMedia != null) {
                    downloadedMedia.setHasIconForProgress(true);
                    downloadedMedia.setProgress(null);
                }
            } else {
                Media downloadedMedia = mediaProgressHelper.getMedia(mediaId);
                if (downloadedMedia != null) {
                    downloadedMedia.getFiles().get(0).setUrl(url);
                    downloadedMedia.setHasIconForProgress(false);
                    downloadedMedia.setProgress(null);

                    message.getMedias().put(downloadedMedia.getId(), downloadedMedia);
                }
                messageRepository.updateMedias(message.getId(), message.getChatId(), message.getMedias(), false);

                Intent intent = new Intent(ACTION_MEDIA_DOWNLOAD_SUCCESS);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
                sendBroadcast(intent);
            }
        } else {
            Media downloadedMedia = mediaProgressHelper.getMedia(mediaId);
            if (downloadedMedia != null) {
                downloadedMedia.setHasIconForProgress(false);
                downloadedMedia.setProgress(null);
            }
            Intent intent = new Intent(ACTION_MEDIA_DOWNLOAD_SUCCESS);
            intent.putExtra(ChatActivity.CHAT_ID_EXTRA, message.getChatId());
            intent.putExtra(ChatActivity.MEDIA_ID_EXTRA, mediaId);
            sendBroadcast(intent);
        }
        mediaProgressHelper.remove(mediaId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        downloadDisposable.dispose();
        if (progressDisposable != null)
            progressDisposable.dispose();
        Log.i("my_logs", "onDestroy download media");
    }
}
