package com.bmstu.vok20.VK;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bmstu.vok20.Helpers.NotificationHelper;
import com.bmstu.vok20.Helpers.UrlDownloader;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anthony on 03.11.16.
 */

public class VKLongPollService extends IntentService {
    private final static String TAG = VKLongPollService.class.getSimpleName();

    private static final String ACTION_UPDATE_MESSAGES = "ACTION_VK_UPDATE_MESSAGES";
    public static final String ACTION_VK_NEW_MESSAGE = "ACTION_VK_NEW_MESSAGE";

    private static final String VK_MESSAGES_GET_LONG_POLL_SERVER_METHOD = "messages.getLongPollServer";

    private static final String VK_PARAM_USE_SSL = "use_ssl";
    private static final String VK_PARAM_NEED_PTS = "need_pts";

    private static final String VK_GET_PARAM_KEY = "&key=";
    private static final String VK_GET_PARAM_TS = "&ts=";
    private static final String VK_GET_PARAM_TIMEOUT = "&wait=";

    private static final int VK_LONG_PULL_USE_SSL = 0;
    private static final int VK_LONG_PULL_NEED_PTS = 0;
    private static final int VK_LONG_PULL_TIMEOUT = 25;

    private static final int VK_NEW_MESSAGE_EVENT_CODE = 4;

    private String key;
    private String server;
    private int ts;

    public VKLongPollService() {
        super("VKLongPollService");
    }

    public static void startActionUpdateMessages(Context context) {
        Intent intent = new Intent(context, VKLongPollService.class);
        intent.setAction(ACTION_UPDATE_MESSAGES);
        context.startService(intent);
    }

    private void sendNewMessageBroadcast(String msgBody) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(ACTION_VK_NEW_MESSAGE);
        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext(), msgBody);
        notificationHelper.sendNotificationNewMsg();
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_MESSAGES: {
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
                        VK_PARAM_USE_SSL, VK_LONG_PULL_USE_SSL,
                        VK_PARAM_NEED_PTS, VK_LONG_PULL_NEED_PTS
                )
        );

        getLongPollServerRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                try {
                    JSONObject responseJSON = response.json.getJSONObject("response");
                    key = responseJSON.getString("key");
                    server = responseJSON.getString("server");
                    ts = responseJSON.getInt("ts");

                    longPollRequester();
                } catch (JSONException e) {
                    Log.d(TAG, "Can not parse JSON", e);
                }
            }
        });
    }

    private void longPollRequester() {
        String longPollServerUrl = buildVkLongPollServerUrl(server, key, ts);

        UrlDownloader.getInstance().load(longPollServerUrl);
        UrlDownloader.getInstance().setCallback(new UrlDownloader.Callback() {
            @Override
            public void onLoaded(String key, String value) {
                if (value == null) {
                    Log.d(TAG, "Long Poll null response");
                } else {
                    Log.d(TAG, "Long Poll response is: " + value);

                    try {
                        JSONObject longPollResponse = new JSONObject(value);

                        setTs(longPollResponse.getInt("ts"));

                        JSONArray longPollUpdates = longPollResponse.getJSONArray("updates");
                        for (int i = 0; i < longPollUpdates.length(); i++) {

                            if (longPollUpdates.getJSONArray(i).getInt(0) == VK_NEW_MESSAGE_EVENT_CODE) {
                                sendNewMessageBroadcast(longPollUpdates.getJSONArray(i).getString(6));
                                Log.d(TAG, "New VK message: " + longPollUpdates.getJSONArray(i).toString());
                            }
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "Can not parse JSON", e);
                    }
                }

                longPollRequester();
            }
        });
    }

    private String buildVkLongPollServerUrl(String server, String key, int ts) {
        StringBuilder serverUrlBuilder = new StringBuilder();

        serverUrlBuilder
                .append("https://")
                .append(server)
                .append("?act=a_check")
                .append(VK_GET_PARAM_KEY)
                .append(key)
                .append(VK_GET_PARAM_TS)
                .append(ts)
                .append(VK_GET_PARAM_TIMEOUT)
                .append(VK_LONG_PULL_TIMEOUT)
                .append("mode=2&version=1");

        return serverUrlBuilder.toString();
    }

    private void setTs(int ts) {
        this.ts = ts;
    }
}
