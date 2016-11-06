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

import com.bmstu.vok20.DatabaseHelper;
import com.bmstu.vok20.R;
import com.bmstu.vok20.Utils;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.bmstu.vok20.VK.VKLongPollService.startActionUpdateMessages;

/**
 * Created by anthony on 02.11.16.
 */

public class VKDialogsFragment extends Fragment {
    private final static String TAG = VKDialogsFragment.class.getSimpleName();

    private final static int DIALOGS_COUNT = 20;

    private String[] scope;

    private DatabaseHelper databaseHelper;

    private View vkDialogsView;
    private ListView vkDialogsListView;
    private VKDialogsAdapter vkDialogsAdapter;

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

        vkDialogsListView = (ListView) vkDialogsView.findViewById(R.id.vkDialogList);

        if (!Utils.isOnline(getActivity())) {
            getVKDialogsFromDB();
        } else {
            if (VKSdk.isLoggedIn()) {
                getVKDialogs();
            } else {
                VKSdk.login(getActivity(), scope);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "VK Login");
        getVKDialogs();
    }

    public void getVKDialogsFromDB() {
        ArrayList<VKDialog> dialogs = new ArrayList<>();

        try {
            Dao<VKDialog, Integer> vkDialogDao = getHelper().getVkDialogDao();

//            QueryBuilder<VKDialog, Integer> queryBuilder = vkDialogDao.queryBuilder();
            List<VKDialog> vkDialogList = vkDialogDao.queryForAll();
            for (VKDialog dialog : vkDialogList) {
                dialogs.add(dialog);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Cannot read dialogs from DB", e);
        }

        vkDialogsAdapter = new VKDialogsAdapter(getActivity(), dialogs);
        vkDialogsListView.setAdapter(vkDialogsAdapter);
    }

    private void getVKDialogs() {
        final ArrayList<VKDialog> dialogs = new ArrayList<>();

        VKRequest dialogsRequest = VKApi.messages().getDialogs(
                VKParameters.from(VKApiConst.COUNT, DIALOGS_COUNT)
        );

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
                    dialogs.add(new VKDialog(
                        userId, dialog.message.title, dialog.unread, dialog.message.body, dialog.message.date
                    ));
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

                        // Удаляем старые диалоги
                        try {
                            Dao<VKDialog, Integer> vkDialogDao = getHelper().getVkDialogDao();
                            DeleteBuilder<VKDialog, Integer> deleteBuilder = vkDialogDao.deleteBuilder();
                            deleteBuilder.delete();
                        } catch (SQLException e) {
                            Log.e(TAG, "Cannot delete dialogs from DB", e);
                        }

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

                            // TODO: Обновляем текущие, а не удалить-добавитьзаного
                            // Добавляем новые диалоги
                            try {
                                Dao<VKDialog, Integer> vkDialogDao = getHelper().getVkDialogDao();
                                vkDialogDao.create(dialog);
                            } catch (SQLException e) {
                                Log.e(TAG, "Cannot add dialog to DB to user: " + dialog.getUserId(), e);
                            }
                        }   // for (VKDialog dialog : dialogs)

                        vkDialogsAdapter = new VKDialogsAdapter(getActivity(), dialogs);
                        vkDialogsListView.setAdapter(vkDialogsAdapter);
                        VKLongPollService vkLongPollService = new VKLongPollService();
                        vkLongPollService.startActionUpdateMessages(getActivity(), 1);
                    }
                });
            }
        });
    }   // getVKDialogs()

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper =
                    OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
