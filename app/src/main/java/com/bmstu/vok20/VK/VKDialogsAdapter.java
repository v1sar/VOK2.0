package com.bmstu.vok20.VK;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmstu.vok20.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anthony on 02.11.16.
 */

public class VKDialogsAdapter extends BaseAdapter {
    private final static String TAG = VKDialogsAdapter.class.getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<VKDialog> dialogs;

    public VKDialogsAdapter(Context context, ArrayList<VKDialog> dialogs) {
        this.context = context;
        this.dialogs = dialogs;
        this.layoutInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dialogs.size();
    }

    @Override
    public Object getItem(int position) {
        return dialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.vk_dialog_item, parent, false);
        }

        VKDialog dialog = getDialog(position);

        ((TextView) view.findViewById(R.id.vkDialogTitle)).setText(dialog.getTitle());
        ((TextView) view.findViewById(R.id.vkDialogLastMessageBody)).setText(dialog.getLastMessageBody());

        TextView unreadView = (TextView) view.findViewById(R.id.vkDialogUnread);
        int unread = dialog.getUnread();

        if (unread == 0) {
            unreadView.setVisibility(View.INVISIBLE);
        } else {
            unreadView.setText(String.valueOf(unread));
            unreadView.setVisibility(View.VISIBLE);
        }

        ImageView avatarView = (ImageView) view.findViewById(R.id.vkDialogAvatar);

        if (dialog.getAvatarUrl().equals("")) {
            Picasso
                    .with(context)
                    .load(R.mipmap.ic_camera_200)
                    .into(avatarView);
        } else {
            Picasso
                    .with(context)
                    .load(dialog.getAvatarUrl())
                    .into(avatarView);
        }

        view.setTag(dialog.getUserId());
        view.setOnClickListener(onClickDialogListener);

        return view;
    }

    private VKDialog getDialog(int position) {
        return (VKDialog) getItem(position);
    }

    private View.OnClickListener onClickDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int userId = (int) v.getTag();
            showVKMessagesFragment(userId);
        }
    };

    private void showVKMessagesFragment(int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", userId);

        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = new VKMessagesFragment();
        fragment.setArguments(bundle);

        Fragment dialogs = fragmentManager.findFragmentById(R.id.content_main);
        if (dialogs != null && dialogs.isAdded()) {
            transaction.remove(dialogs);
        }

        transaction.replace(R.id.content_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
