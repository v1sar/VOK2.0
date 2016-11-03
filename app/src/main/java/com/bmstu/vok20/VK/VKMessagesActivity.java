package com.bmstu.vok20.VK;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bmstu.vok20.MainActivity;
import com.bmstu.vok20.R;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiMessages;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by anthony on 03.11.16.
 */

public class VKMessagesActivity extends AppCompatActivity {
    private final static String TAG = VKMessagesActivity.class.getSimpleName();

    private static final String MESSAGES_GET_HISTORY_METHOD = "messages.getHistory";
    private static final String MESSAGES_SEND_METHOD = "messages.send";

    private static final int MESSAGES_COUNT = 30;
    private static final int MESSAGES_REVERSE = 0;

    private int userId;
    private VKMessagesAdapter messagesAdapter;
    private ArrayList<VKMessage> messages = new ArrayList<VKMessage>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vk_messages_activity);

        userId = getIntent().getIntExtra("id", 0);
        ListView vkMessagesList = (ListView) findViewById(R.id.vkMessagesList);
        getVKMessageHistory(vkMessagesList);

        Button sendMessageBtn = (Button) findViewById(R.id.vkSendMessageBtn);
        sendMessageBtn.setOnClickListener(sendButtonClickListener);

        VKLongPollService.startActionUpdateMessages(VKMessagesActivity.this, userId);
    }

    private void getVKMessageHistory(final ListView messagesListView) {
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
                for (VKApiMessage message : messageList) {
                    messages.add(new VKMessage(message.body, message.out));
                }

                messagesAdapter = new VKMessagesAdapter(VKMessagesActivity.this, messages);

                messagesListView.setAdapter(messagesAdapter);
            }
        });
    }

    View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String messageText = ((EditText) findViewById(R.id.vkSendMessageInput)).getText().toString();
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

                EditText messageInput = (EditText) findViewById(R.id.vkSendMessageInput);
                messageInput.setText("");

                //messages.add(new VKMessage(messageText, true));
                //messagesAdapter.updateList(messages);

                Log.d(TAG, "Message send to user" + userId);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                messages.remove(messages.size() - 1);
            }
        });
    }   // sendVKMessage
}
