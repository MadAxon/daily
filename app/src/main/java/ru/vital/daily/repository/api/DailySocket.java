package ru.vital.daily.repository.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.vital.daily.activity.ChatActivity;
import ru.vital.daily.enums.Operation;
import ru.vital.daily.repository.api.response.SocketResponse;
import ru.vital.daily.util.StaticData;

@Singleton
public class DailySocket {

    private final String SOCKET_URL = "https://dev.daily1.ru:444",
            CONNECTION = "connection",
            AUTH_SIGN_IN = "auth/login",
            LOCATION_UPDATE = "geo/location/update",
            MESSAGE_SEND = "chat/message/send",
            MESSAGE_TYPE = "chat/message/typing",
            MESSAGE_READ = "chat/message/read",
            MESSAGE_REMOVE = "chat/message/remove",
            PROFILE_ONLINE = "profile/online";

    private Socket socket;

    private final Context context;

    @Inject
    public DailySocket(Context context) {
        this.context = context;
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"};
            options.path = "/sockets";
            this.socket = IO.socket(SOCKET_URL, options);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void connect(String accessKey) {
        Log.i("my_logs", "trying to connect to socket...");
        socket.once(CONNECTION, args -> {
            emitSignIn(accessKey);
            Log.i("my_logs", "socket connection is successful");
        });
        socket.on(LOCATION_UPDATE, args -> {
            try {
                SocketResponse response = LoganSquare.parse(args[0].toString(), SocketResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socket.on(MESSAGE_SEND, args -> {
            try {
                Log.i("my_logs", "MESSAGE_SEND ");
                SocketResponse response = LoganSquare.parse(args[0].toString(), SocketResponse.class);
                Intent intent = new Intent(Operation.ACTION_MESSAGE_SEND);
                intent.putExtra(ChatActivity.MESSAGE_IDS_EXTRA, response.getChatMessageIds());
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, response.getChatId());
                context.sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socket.on(MESSAGE_TYPE, args -> {
            try {
                SocketResponse response = LoganSquare.parse(args[0].toString(), SocketResponse.class);
                Intent intent = new Intent(Operation.ACTION_MESSAGE_TYPE);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, response.getChatId());
                intent.putExtra(ChatActivity.ACCOUNT_ID_EXTRA, response.getAccountId());
                context.sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socket.on(MESSAGE_READ, args -> {
            try {
                SocketResponse response = LoganSquare.parse(args[0].toString(), SocketResponse.class);
                Intent intent = new Intent(Operation.ACTION_MESSAGE_READ);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, response.getChatId());
                intent.putExtra(ChatActivity.MESSAGE_IDS_EXTRA, response.getChatMessageIds());
                intent.putExtra(ChatActivity.ACCOUNT_ID_EXTRA, response.getAccountId());
                context.sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socket.on(MESSAGE_REMOVE, args -> {
            try {
                SocketResponse response = LoganSquare.parse(args[0].toString(), SocketResponse.class);
                Intent intent = new Intent(Operation.ACTION_MESSAGE_DELETE);
                intent.putExtra(ChatActivity.CHAT_ID_EXTRA, response.getChatId());
                intent.putExtra(ChatActivity.MESSAGE_IDS_EXTRA, response.getChatMessageIds());
                intent.putExtra(ChatActivity.ACCOUNT_ID_EXTRA, response.getAccountId());
                context.sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socket.on(PROFILE_ONLINE, args -> {
            try {
                SocketResponse response = LoganSquare.parse(args[0].toString(), SocketResponse.class);
                Intent intent = new Intent(Operation.ACTION_PROFILE_ONLINE);
                intent.putExtra(ChatActivity.ACCOUNT_ID_EXTRA, response.getAccountId());
                context.sendBroadcast(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        socket.connect();
    }

    public void emitSignIn(String accessKey) {
        if (socket != null && socket.connected())
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accessKey", accessKey);
                socket.emit(AUTH_SIGN_IN, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void emitLocationUpdate(Double[] coords) {
        if (socket != null && socket.connected()) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray(coords);
                jsonObject.put("coords", jsonArray);
                socket.emit(LOCATION_UPDATE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void emitSendMessage(long[] messageIds, Long chatId) {
        if (socket != null && socket.connected())
            try {
                Log.i("my_logs", "emitSendMessage " + Arrays.toString(messageIds) + " chatId " + chatId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("chatId", chatId);
                JSONArray jsonArray = new JSONArray(messageIds);
                jsonObject.put("chatMessageIds", jsonArray);
                socket.emit(MESSAGE_SEND, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void emitTypeMessage(Long chatId) {
        if (socket != null && socket.connected())
            try {
                Log.i("my_logs", "emitTypeMessage " + chatId);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("chatId", chatId);
                socket.emit(MESSAGE_TYPE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void emitReadMessage(Long chatId, long[] chatMessageIds) {
        if (socket != null && socket.connected())
            try {
                Log.i("my_logs", "emitReadMessage " + Arrays.toString(chatMessageIds));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("chatId", chatId);
                JSONArray jsonArray = new JSONArray(chatMessageIds);
                jsonObject.put("chatMessageIds", jsonArray);
                socket.emit(MESSAGE_READ, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void emitRemoveMessage(Long chatId, long[] chatMessageIds) {
        if (socket != null && socket.connected())
            try {
                Log.i("my_logs", "emitRemoveMessage " + Arrays.toString(chatMessageIds));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("chatId", chatId);
                JSONArray jsonArray = new JSONArray(chatMessageIds);
                jsonObject.put("chatMessageIds", jsonArray);
                socket.emit(MESSAGE_REMOVE, jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void reconnect() {
        if (socket != null && !socket.connected() && StaticData.getData() != null) {
            connect(StaticData.getData().key.getAccessKey());
            Log.i("my_logs", "socket reconnect");
        }
    }

    public void disconnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            socket.off();
            Log.i("my_logs", "socket disconnect");
        }
    }

}
