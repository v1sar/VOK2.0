package com.bmstu.vok20.VK;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bmstu.vok20.R;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VKUsersFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;

    private static final String USERS_TO_FIND = "users.search";
    private final static int USERS_COUNT = 20;
    private static String query;
    private List<VKUsers> foundUsers = new ArrayList<VKUsers>();

    public VKUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vkusers, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.find_users_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = ((EditText) view.findViewById(R.id.users_query)).getText().toString();
                VKRequest findRequest = new VKRequest(
                        USERS_TO_FIND,
                        VKParameters.from(
                                VKApiConst.Q, query,
                                VKApiConst.COUNT, USERS_COUNT
                        )
                );
                findRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        JSONArray messageJSONItems = null;
                        try {
                            messageJSONItems = response.json.getJSONObject("response").getJSONArray("items");
                            for (int i = 0; i < messageJSONItems.length(); i++) {
                                VKUsers tempUser = new VKUsers(messageJSONItems.getJSONObject(i).getString("first_name"),
                                        messageJSONItems.getJSONObject(i).getString("last_name"));
                                foundUsers.add(tempUser);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        findUsers(foundUsers);
                    }
                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                    }
                });
            }
        });
    }

    void findUsers(List<VKUsers> foundUsers){
        recylerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.find_users);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerViewAdapter = new VKUsersAdapter(getActivity(), foundUsers);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
