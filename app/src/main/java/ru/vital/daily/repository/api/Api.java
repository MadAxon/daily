package ru.vital.daily.repository.api;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import ru.vital.daily.repository.api.request.AddMembersRequest;
import ru.vital.daily.repository.api.request.EmailRequest;
import ru.vital.daily.repository.api.request.EmptyRequest;
import ru.vital.daily.repository.api.request.IdRequest;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.request.PhoneRequest;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.repository.model.SyncContactsModel;
import ru.vital.daily.repository.api.response.BaseResponse;
import ru.vital.daily.repository.api.response.ItemResponse;
import ru.vital.daily.repository.data.Key;
import ru.vital.daily.repository.data.User;
import ru.vital.daily.repository.model.CodeHashModel;
import ru.vital.daily.repository.model.ProfileSaveModel;

public interface Api {

    @POST("client/auth/request_sms")
    Single<ItemResponse<CodeHashModel>> requestSms(@Body PhoneRequest request);

    @POST("client/auth/request_mail")
    Single<ItemResponse<CodeHashModel>> requestEmail(@Body EmailRequest request);

    @POST("client/auth/login/phone")
    Single<ItemResponse<Key>> signInPhone(@Body PhoneRequest request);

    @POST("client/auth/login/email")
    Single<ItemResponse<Key>> signInEmail(@Body EmailRequest request);

    @POST("client/profile/save")
    Single<ItemResponse<User>> saveProfile(@Body ItemRequest<ProfileSaveModel> request);

    @Multipart
    @POST("client/media/upload")
    Single<ItemResponse<Media>> uploadMedia(@Part MultipartBody.Part file, @Part("accessKey")RequestBody accessKey, @Part("type")RequestBody type);

    @POST("client/account/upload_contacts")
    Single<BaseResponse> syncContacts(@Body SyncContactsModel request);

    @POST("client/account/get_list")
    Single<ItemsResponse<User>> getUsers(@Body ItemsRequest request);

    @POST("client/profile/get")
    Maybe<ItemResponse<User>> getProfile(@Body EmptyRequest request);

    @POST("client/chat/save")
    Single<ItemResponse<Chat>> saveChat(@Body ItemRequest<ChatSaveModel> request);

    @POST("client/chat/add_member")
    Single<BaseResponse> addMembersToChat(@Body AddMembersRequest request);

    @POST("client/chat/get")
    Maybe<ItemResponse<Chat>> getChat(@Body IdRequest request);

}
