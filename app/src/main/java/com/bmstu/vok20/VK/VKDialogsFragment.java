package com.bmstu.vok20.VK;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bmstu.vok20.R;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by anthony on 02.11.16.
 */

public class VKDialogsFragment extends Fragment {
    private final static String TAG = VKDialogsFragment.class.getSimpleName();
    private final static int DIALOG_COUNT = 15;

    private String[] scope;
    private View vkDialogsView;
    private VKDialogsAdapter dialogsAdapter;

    public VKDialogsFragment() {
        scope = new String[]{VKScope.MESSAGES};
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vk_dialogs_fragment, container, false);
        vkDialogsView = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VKSdk.login(getActivity(), scope);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "VK Login");

        ListView dialogList = (ListView) vkDialogsView.findViewById(R.id.vkDialogList);
        getVKDialogs(dialogList);
    }

    private void getVKDialogs(final ListView dialogListView) {
        VKRequest dialogsRequest = VKApi.messages().getDialogs(
                VKParameters.from(VKApiConst.COUNT, DIALOG_COUNT)
        );

        final ArrayList<VKDialog> dialogs = new ArrayList<>();

        dialogsRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                // Формируем список диалогов без имен и аватарок
                VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;
                VKList<VKApiDialog> dialogList = getMessagesResponse.items;
                StringBuilder userIdsBuilder = new StringBuilder();
                int userId;
                for (VKApiDialog dialog : dialogList) {
                    userId = dialog.message.user_id;
                    dialogs.add(new VKDialog(userId, dialog.message.title, dialog.message.body));
                    userIdsBuilder.append(String.valueOf(userId));
                    userIdsBuilder.append(", ");
                }
                String userIds = userIdsBuilder.toString();

                // Подгружаем имена и аватарки
                VKRequest usersRequest = VKApi.users().get(
                        VKParameters.from(
                                VKApiConst.USER_IDS, userIds,
                                VKApiConst.FIELDS, "photo_200"
                        )
                );

                usersRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        VKList users = (VKList) response.parsedModel;
                        VKApiModel user;
                        for (VKDialog dialog : dialogs) {
                            user = users.getById(dialog.getUserId());

                            if (dialog.getTitle().equals(" ... ")) {
                                StringBuilder username = new StringBuilder();
                                try {
                                    username.append(user.fields.get("first_name").toString());
                                    username.append(' ');
                                    username.append(user.fields.get("last_name").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                dialog.setTitle(username.toString());
                            }

                            try {
                                dialog.setAvatarUrl(user.fields.get("photo_200").toString());
                            } catch (JSONException e) {
                                // e.printStackTrace();
                                Log.w(TAG, "Photo missing, user" + dialog.getUserId());
                            }
                        }

                        dialogsAdapter = new VKDialogsAdapter(getActivity(), dialogs);
                        dialogListView.setAdapter(dialogsAdapter);
                    }
                });
            }
        });
    }
}
