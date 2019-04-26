package ru.vital.daily.repository;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Part;
import ru.vital.daily.repository.api.Api;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.db.MediaDao;
import ru.vital.daily.util.DisposableProvider;

@Singleton
public class MediaRepository {

    private final Api api;

    private final MediaDao mediaDao;

    @Inject
    public MediaRepository(Api api, MediaDao mediaDao) {
        this.api = api;
        this.mediaDao = mediaDao;
    }

    public Single<ItemResponse<Media>> uploadMedia(@Part MultipartBody.Part file, @Part("accessKey") RequestBody accessKey, @Part("type")RequestBody type) {
        return api.uploadMedia(file, accessKey, type);
    }

    public void saveMedia(Media media) {
        DisposableProvider.doCallable(() -> {
            mediaDao.insert(media);
            return null;
        });
    }
}
