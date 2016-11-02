package com.bmstu.vok20.VK;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bmstu.vok20.R;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

/**
 * Created by anthony on 02.11.16.
 */

public class VKDialogsFragment extends Fragment {
    private final static String TAG = VKDialogsFragment.class.getSimpleName();

    private String[] scope;
    protected View VKDialogsView;

    public VKDialogsFragment() {
        scope = new String[]{VKScope.MESSAGES};
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vk_dialogs_fragment, container, false);
        VKDialogsView = view;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VKSdk.login(getActivity(), scope);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "VK Login");
    }
}
