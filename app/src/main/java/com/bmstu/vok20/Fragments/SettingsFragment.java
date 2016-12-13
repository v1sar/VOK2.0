package com.bmstu.vok20.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bmstu.vok20.Helpers.PreferenceHelper;
import com.bmstu.vok20.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.invis_check);

        if (PreferenceHelper.getInstance().getBoolean(PreferenceHelper.ENABLE_INVISIBLE))
            checkBox.setChecked(true);
        else checkBox.setChecked(false);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    PreferenceHelper.getInstance().putBoolean(PreferenceHelper.ENABLE_INVISIBLE, true);
                } else {
                    PreferenceHelper.getInstance().putBoolean(PreferenceHelper.ENABLE_INVISIBLE, false);
                }
            }
        });

        return view;
    }

}
