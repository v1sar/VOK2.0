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
        ((TextView) view.findViewById(R.id.vkDialogLastMessage)).setText(dialog.getLastMessage());

        ImageView avatarView = (ImageView) view.findViewById(R.id.vkDialogAvatar);
        getAvatarFromSDorUpload(avatarView, dialog.getUserId(), dialog.getAvatarUrl());

        view.setId(dialog.getUserId());
        view.setOnClickListener(onClickDialogListener);

        return view;
    }

    private VKDialog getDialog(int position) {
        return (VKDialog) getItem(position);
    }

    private View.OnClickListener onClickDialogListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int userId = v.getId();
            showVKMessagesFragment(userId);
        }
    };

    private void showVKMessagesFragment(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", id);

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

    /******** Блок работы с SD ******/
    private static class AsyncAvatarHelper {
        ImageView imageView;
        int userId;
        String imageUrl;
    }

    private void getAvatarFromSDorUpload(ImageView imageView, int userId, String imageUrl) {
        Bitmap avatar = getAvatarFromSD(userId);

        if (avatar == null) {
            AsyncAvatarHelper avatarHelper = new AsyncAvatarHelper();
            avatarHelper .userId = userId;
            avatarHelper .imageView = imageView;
            avatarHelper .imageUrl = imageUrl;

            new UploadAvatarToSD().execute(avatarHelper);
        } else {
            imageView.setImageBitmap(avatar);
        }
    }

    private String getAvatarPath(int userId) {
        StringBuilder appDirPath = new StringBuilder();

        appDirPath
                .append(Environment.getExternalStorageDirectory())
                .append(File.separator)
                .append(R.string.app_name);

        File appDir = new File(appDirPath.toString());
        Log.d(TAG, "App dir: " + appDir.toString());

        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        StringBuilder imagePath = new StringBuilder();

        imagePath
                .append(appDir.toString())
                .append(File.separator)
                .append(userId)
                .append(".jpg");

        Log.d(TAG, "Image path: " + imagePath.toString());

        return imagePath.toString();
    }

    // Взять аватар с SD-карты
    private Bitmap getAvatarFromSD(int userId) {
        File imageFile = new File(getAvatarPath(userId));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        try {
            return BitmapFactory.decodeStream(new FileInputStream(imageFile), null, options);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    // Асинхронно подгружаем аватарки и сохраняем на SD-карту
    private class UploadAvatarToSD extends AsyncTask<AsyncAvatarHelper, Void, Bitmap> {
        AsyncAvatarHelper avatar;

        @Override
        protected Bitmap doInBackground(AsyncAvatarHelper... params) {
            avatar = params[0];

            try {
                return BitmapFactory.decodeStream(
                        (InputStream) new URL(avatar.imageUrl).getContent()
                );
            } catch (IOException e) {
                // e.printStackTrace();
                Log.w(TAG, "Avatar url is not valid, user" + avatar.userId);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap resultBitmap) {
            super.onPostExecute(resultBitmap);

            if (resultBitmap == null) {
                return;
            }

            avatar.imageView.setImageBitmap(resultBitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            File imageFile = new File(getAvatarPath(avatar.userId));
            try {
                imageFile.createNewFile();

                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                fileOutputStream.write(bytes.toByteArray());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }   // UploadAvatarToSD
    /********* **************/
}
