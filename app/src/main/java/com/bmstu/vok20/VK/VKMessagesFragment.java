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

import com.bmstu.vok20.R;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

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
    private View vkMessagesView;
    private VKMessagesAdapter messagesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vkMessagesView = inflater.inflate(R.layout.vk_messages_fragment, container, false);
        userId = getArguments().getInt("id");
        return vkMessagesView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ListView vkMessagesList = (ListView) vkMessagesView.findViewById(R.id.vkMessagesList);
        getVKMessageHistory(vkMessagesList);
        Button sendMessageBtn = (Button) vkMessagesView.findViewById(R.id.vkSendMessageBtn);
        sendMessageBtn.setOnClickListener(sendButtonClickListener);
        super.onViewCreated(view, savedInstanceState);
    }

    private void getVKMessageHistory(final ListView messagesListView) {
        final ArrayList<VKMessage> messages = new ArrayList<VKMessage>();

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

                messagesAdapter = new VKMessagesAdapter(getActivity(), messages);

                messagesListView.setAdapter(messagesAdapter);
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

    private void sendVKMessage(String messageText) {
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

                Log.d(TAG, "Message send to user" + userId);
            }
        });
    }
}
