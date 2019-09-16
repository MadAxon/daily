package ru.vital.daily.repository.api;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import ru.vital.daily.repository.api.request.JoinRequest;
import ru.vital.daily.repository.api.request.EmailRequest;
import ru.vital.daily.repository.api.request.EmptyJson;
import ru.vital.daily.repository.api.request.IdRequest;
import ru.vital.daily.repository.api.request.ItemRequest;
import ru.vital.daily.repository.api.request.ItemsRequest;
import ru.vital.daily.repository.api.request.MessageForwardRequest;
import ru.vital.daily.repository.api.request.MessageReadRequest;
import ru.vital.daily.repository.api.request.MessageRemoveRequest;
import ru.vital.daily.repository.api.request.MessageRequest;
import ru.vital.daily.repository.api.request.MessagesRequest;
import ru.vital.daily.repository.api.request.PhoneRequest;
import ru.vital.daily.repository.api.response.ItemsResponse;
import ru.vital.daily.repository.data.Chat;
import ru.vital.daily.repository.data.Media;
import ru.vital.daily.repository.data.Message;
import ru.vital.daily.repository.model.ChatSaveModel;
import ru.vital.daily.repository.model.MediaEditModel;
import ru.vital.daily.repository.model.MessageSendModel;
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
    Single<ItemResponse<Media>> uploadMedia(@Part MultipartBody.Part file, @Part("type")RequestBody type);

    @Multipart
    @POST("client/media/upload")
    Single<ItemResponse<Media>> uploadMedia(@Part MultipartBody.Part file, @Part("type")RequestBody type, @Part("description")RequestBody description);

    @POST("client/account/upload_contacts")
    Single<BaseResponse<EmptyJson>> syncContacts(@Body SyncContactsModel request);

    @POST("client/account/get_list")
    Single<ItemsResponse<User>> getUsers(@Body ItemsRequest request);

    @POST("client/profile/get")
    Maybe<ItemResponse<User>> getProfile(@Body EmptyJson request);

    @POST("client/chat/save")
    Single<ItemResponse<Chat>> saveChat(@Body ItemRequest<ChatSaveModel> request);

    @POST("client/chat/join")
    Single<BaseResponse<EmptyJson>> join(@Body JoinRequest request);

    @POST("client/chat/get")
    Maybe<ItemResponse<Chat>> getChat(@Body IdRequest request);

    @POST("client/chat/get_list")
    Single<ItemsResponse<Chat>> getChats(@Body ItemsRequest request);

    @POST("client/chat/message/get_list")
    Single<ItemsResponse<Message>> getMessages(@Body MessagesRequest request);

    @POST("client/chat/remove")
    Single<BaseResponse<EmptyJson>> removeChat(@Body IdRequest request);

    @POST("client/chat/message/send")
    Single<ItemResponse<Message>> sendMessage(@Body ItemRequest<MessageSendModel> request);

    @POST("client/chat/message/get")
    Single<ItemResponse<Message>> getMessage(@Body MessageRequest request);

    @POST("client/chat/message/remove")
    Single<BaseResponse<EmptyJson>> deleteMessages(@Body MessageRemoveRequest request);

    @POST("client/account/get")
    Single<ItemResponse<User>> getUser(@Body IdRequest request);

    @POST("client/chat/message/read")
    Single<BaseResponse<EmptyJson>> readMessage(@Body MessageReadRequest request);

    @POST("client/media/edit")
    Single<ItemResponse<Media>> editMedia(@Body ItemRequest<MediaEditModel> request);

    @POST("client/chat/message/forward")
    Single<ItemsResponse<Message>> forwardMessages(@Body MessageForwardRequest request);

}
