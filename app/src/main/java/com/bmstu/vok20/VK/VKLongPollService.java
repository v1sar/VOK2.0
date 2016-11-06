package com.bmstu.vok20.VK;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

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
                 //   int userId = intent.getIntExtra(USER_ID_PARAM, 0);
                    handleActionUpdateMessages();
                    break;
                }
            }
        }
    }

    private void handleActionUpdateMessages() {
        VKRequest getLongPollServerRequest = new VKRequest(
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

                String key = "";
                String server = "";
                int ts = 0;
                int pts = 0;

                try {
                    JSONObject responseJSON = response.json.getJSONObject("response");
                    key = responseJSON.getString("key");
                    server = responseJSON.getString("server");
                    ts = responseJSON.getInt("ts");
                    pts = responseJSON.getInt("pts");

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("key= "+key)
                    .append(", server= " + server)
                    .append(", ts= " + ts)
                    .append(", pts= " + pts);
                    Log.d(TAG, stringBuilder.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                VKRequest getLongPollHistoryRequest = new VKRequest(
                        VK_MESSAGES_GET_LONG_POLL_HISTORY_METHOD,
                        VKParameters.from("server", server,
                                "key", key,
                                VK_PARAM_TS, ts,
                                "pts", pts,
                                "wait", 10,
                                "mode", 2)
                );

                getLongPollHistoryRequest.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        Log.d(TAG, "we are here");
                        try {
                            JSONObject responseJSON = response.json.getJSONObject("response");
                            Log.d(TAG, responseJSON.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                        Log.d(TAG, "error: " + error);
                    }

                });
            }
        });     // getLongPollServerRequest.execute
    }
}
