package com.bmstu.vok20.VK;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bmstu.vok20.UrlDownloader;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKHttpClient;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiPoll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by anthony on 03.11.16.
 */

public class VKLongPollService extends IntentService {
    private final static String TAG = VKLongPollService.class.getSimpleName();

    private static final String ACTION_UPDATE_MESSAGES = "ACTION_VK_UPDATE_MESSAGES";
    private static final String USER_ID_PARAM = "VK_USER_ID_PARAM";

    private static final String VK_MESSAGES_GET_LONG_POLL_SERVER_METHOD = "messages.getLongPollServer";
    private static final String VK_MESSAGES_GET_LONG_POLL_HISTORY_METHOD = "messages.getLongPollHistory";

    private static final String VK_PARAM_USE_SSL = "use_ssl";
    private static final String VK_PARAM_NEED_PTS = "need_pts";
    private static final String VK_PARAM_TS = "ts";
    private static final String VK_PARAM_ONLINES = "onlines";
    private static final String VK_PARAM_MAX_MSG_ID = "max_msg_id";

    public static final String VK_NEW_MESSAGE = "vk_new_message";

    String key;
    String server;
    int ts;
    int pts;

    public VKLongPollService() {
        super("VKLongPollService");
    }

    public static void startActionUpdateMessages(Context context, int userId) {
        Intent intent = new Intent(context, VKLongPollService.class);
        intent.setAction(ACTION_UPDATE_MESSAGES);
        intent.putExtra(USER_ID_PARAM, userId);
        Log.d(TAG, "startActionUpdateMessages");
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_MESSAGES: {
                    Log.d(TAG, "ACTION_UPDATE_MESSAGES");
                    int userId = intent.getIntExtra(USER_ID_PARAM, 0);
                    handleActionUpdateMessages();
                    break;
                }
            }
        }
    }

    private void handleActionUpdateMessages() {
        final VKRequest getLongPollServerRequest = new VKRequest(
                VK_MESSAGES_GET_LONG_POLL_SERVER_METHOD,
                VKParameters.from(
                        VK_PARAM_USE_SSL, 0,
                        VK_PARAM_NEED_PTS, 1
                )
        );
        Log.d(TAG, "Update messages");
        getLongPollServerRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONObject responseJSON = response.json.getJSONObject("response");
                    key = responseJSON.getString("key");
                    server = responseJSON.getString("server");
                    ts = responseJSON.getInt("ts");
                    pts = responseJSON.getInt("pts");
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("key= " + key)
                            .append(", server= " + server)
                            .append(", ts= " + ts)
                            .append(", pts= " + pts);
                    Log.d(TAG, stringBuilder.toString());
                    longPollRequester(server, key, ts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }// getLongPollServerRequest.execute

    /////*************** okhttp3 LongPoll
    private void longPollRequester(String server, String key, int ts) {
        String longPollServerUrl = "https://" + server + "?act=a_check&key=" + key + "&ts=" + ts + "&wait=25&mode=2&version=1";
        UrlDownloader.getInstance().load(longPollServerUrl);
        UrlDownloader.getInstance().setCallback(new UrlDownloader.Callback() {
            @Override
            public void onLoaded(String key, String value) {
                Log.d(TAG, "response is: " + value);
                try {
                    JSONObject longPollResponse = new JSONObject(value);
                    Log.d(TAG, longPollResponse.toString());
                    setTs(longPollResponse.getInt("ts"));
                    JSONArray longPollUpdates = longPollResponse.getJSONArray("updates");
                    for (int i = 0; i < longPollUpdates.length(); i++) {
                        if (longPollUpdates.getJSONArray(i).getInt(0) == 4) {
                            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(VKLongPollService.this);
                            broadcastManager.sendBroadcast(new Intent(VK_NEW_MESSAGE));
                            Log.d(TAG, "NEW MESSAGE" + longPollUpdates.getJSONArray(i).toString());
                        }
                    }
                    longPollRequester(getServer(), getKey(), getTs());
                } catch (Throwable t) {
                    Log.e(TAG, "Cannot parse string to JSON: " + value);
                    longPollRequester(getServer(), getKey(), getTs());
                }
            }
        });
    }
    //////***************

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int getTs() {
        return ts;
    }

    public String getServer() {
        return server;
    }

    public String getKey() {
        return key;
    }
}
