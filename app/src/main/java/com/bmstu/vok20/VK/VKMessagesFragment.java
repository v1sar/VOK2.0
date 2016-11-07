package com.bmstu.vok20.VK;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bmstu.vok20.DatabaseHelper;
import com.bmstu.vok20.R;
import com.bmstu.vok20.Utils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anthony on 03.11.16.
 */

public class VKMessagesFragment extends Fragment {
    private final static String TAG = VKMessagesFragment.class.getSimpleName();

    private static final String MESSAGES_GET_HISTORY_METHOD = "messages.getHistory";
    private static final String MESSAGES_SEND_METHOD = "messages.send";

    private static final int MESSAGES_COUNT = 30;
    private static final int MESSAGES_REVERSE = 0;

    private int userId;

    private DatabaseHelper databaseHelper;

    private View vkMessagesView;
    private ListView vkMessagesListView;
    private VKMessagesAdapter vkMessagesAdapter;
    private ArrayList<VKMessage> messages = new ArrayList<>();

    private Button sendVkMessageBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vkMessagesView = inflater.inflate(R.layout.vk_messages_fragment, container, false);
        userId = getArguments().getInt("user_id");
        return vkMessagesView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vkMessagesListView = (ListView) vkMessagesView.findViewById(R.id.vkMessagesList);
        sendVkMessageBtn = (Button) vkMessagesView.findViewById(R.id.vkSendMessageBtn);

        if (Utils.isOnline(getActivity())) {
            getVKMessageHistory();
        } else {
            getVKMessageHistoryFromDB();
        }

        sendVkMessageBtn.setOnClickListener(sendButtonClickListener);
    }

    private void getVKMessageHistoryFromDB() {
        try {
            Dao<VKMessage, Integer> vkMessageDao = getHelper().getVkMessageDao();

            QueryBuilder<VKMessage, Integer> queryBuilder = vkMessageDao.queryBuilder();
            queryBuilder.where().eq(VKMessage.VK_MESSAGE_USER_ID_FIELD_NAME, userId);
            queryBuilder.orderBy(VKMessage.VK_MESSAGE_TIMESTAMP_FIELD_NAME, true);
            List<VKMessage> vkMessageList = queryBuilder.query();
            for (VKMessage message : vkMessageList) {
                messages.add(message);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Cannot read messages from DB to user: " + userId, e);
        }

        vkMessagesAdapter = new VKMessagesAdapter(getActivity(), messages);
        vkMessagesListView.setAdapter(vkMessagesAdapter);
    }

    private void getVKMessageHistory() {
        VKRequest messagesRequest = new VKRequest(
                MESSAGES_GET_HISTORY_METHOD,
                VKParameters.from(
                        VKApiConst.USER_ID, userId,
                        VKApiConst.COUNT, MESSAGES_COUNT,
                        VKApiConst.REV, MESSAGES_REVERSE
                )
        );

        messagesRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                JSONArray messageJSONItems = null;
                List<VKApiMessage> messageList = new VKList<VKApiMessage>();
                try {
                    messageJSONItems = response.json.getJSONObject("response").getJSONArray("items");
                    for (int i = 0; i < messageJSONItems.length(); i++) {
                        messageList.add(new VKApiMessage(messageJSONItems.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.reverse(messageList);

                // Добавление сообщений в БД
                for (VKApiMessage message : messageList) {
                    try {
                        Dao<VKMessage, Integer> vkMessageDao = getHelper().getVkMessageDao();
                        VKMessage vkMessage = new VKMessage(
                                message.user_id, message.out, message.body, message.date
                        );
                        messages.add(vkMessage);
                        vkMessageDao.create(vkMessage);
                    } catch (SQLException e) {
                        Log.e(TAG, "Cannot add messages to DB to user: " + userId, e);
                    }
                }

                vkMessagesAdapter = new VKMessagesAdapter(getActivity(), messages);
                vkMessagesListView.setAdapter(vkMessagesAdapter);

                // TODO: Проверять соединение перед каждым delete()
                // Удаление сообщений из БД
                if (Utils.isOnline(getActivity())) {
                    try {
                        Dao<VKMessage, Integer> vkMessageDao = getHelper().getVkMessageDao();

                        QueryBuilder<VKMessage, Integer> queryBuilder = vkMessageDao.queryBuilder();
                        queryBuilder.setCountOf(true);
                        queryBuilder.setWhere(queryBuilder.where().eq(
                                VKMessage.VK_MESSAGE_USER_ID_FIELD_NAME, userId
                        ));
                        long existMessagesCount = vkMessageDao.countOf(queryBuilder.prepare());

                        if (existMessagesCount > MESSAGES_COUNT) {
                            DeleteBuilder<VKMessage, Integer> deleteBuilder = vkMessageDao.deleteBuilder();
                            deleteBuilder
                                    .where()
                                    .eq(VKMessage.VK_MESSAGE_USER_ID_FIELD_NAME, userId);

                            // TODO: limit = existMessagesCount - MESSAGES
                            // TODO: orderBy timestamp
                            // deleteBuilder.delete();
                        }
                    } catch (SQLException e) {
                        Log.e(TAG, "Cannot delete messages from DB for user: "+userId, e);
                    }
                }
            }
        });
    }

    View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String messageText = ((EditText) vkMessagesView.findViewById(R.id.vkSendMessageInput)).getText().toString();
            sendVKMessage(messageText);
        }
    };

    private void sendVKMessage(final String messageText) {
        VKRequest sendMessageRequest = new VKRequest(
                MESSAGES_SEND_METHOD,
                VKParameters.from(
                        VKApiConst.USER_ID, userId,
                        VKApiConst.MESSAGE, messageText
                )
        );

        sendMessageRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                EditText messageInput = (EditText) vkMessagesView.findViewById(R.id.vkSendMessageInput);
                messageInput.setText("");
                messages.add(new VKMessage(messageText, true));
                vkMessagesAdapter.updateList(messages);
                Log.d(TAG, "Message send to user" + userId);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                messages.remove(messages.size() - 1);
            }
        });
    }   // sendVKMessage

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
