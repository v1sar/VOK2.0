package com.bmstu.vok20.VK;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmstu.vok20.MainActivity;
import com.bmstu.vok20.R;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKUsersArray;

import java.util.List;

/**
 * Created by qwerty on 14.12.16.
 */

public class VKUsersAdapter extends RecyclerView.Adapter<VKUsersAdapter.ItemViewHolder> {

    List<VKUsers> usersList;
    Context context;
    View view1;
    ItemViewHolder viewHolder1;

    public VKUsersAdapter(Context context, List<VKUsers> usersList){
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view1 = LayoutInflater.from(context).inflate(R.layout.vk_user_item, parent, false);
        viewHolder1 = new ItemViewHolder(view1);
        return viewHolder1;
    }


    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.first_name.setText(usersList.get(position).getFirst_name());
        holder.last_name.setText(usersList.get(position).getLast_name());
        holder.id.setText(String.valueOf(usersList.get(position).getId()));
        if (usersList.get(position).getUrl().equals("")) {
            Picasso
                    .with(context)
                    .load(R.mipmap.ic_camera_200)
                    .into(holder.avatar);
        } else {
            Picasso
                    .with(context)
                    .load(usersList.get(position).getUrl())
                    .into(holder.avatar);
        }

        VKUsers user = usersList.get(position);
        viewHolder1.id.setTag(user.getId());
        viewHolder1.id.setOnClickListener(onClickDialogListener);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView first_name;
        private final TextView last_name;
        private final TextView id;
        private final ImageView avatar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.first_name = (TextView) itemView.findViewById(R.id.found_user_first_name);
            this.last_name = (TextView) itemView.findViewById(R.id.found_user_last_name);
            this.id = (TextView) itemView.findViewById(R.id.found_user_id);
            this.avatar = (ImageView) itemView.findViewById(R.id.vkUserFoundAvatar);

        }

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

        transaction.replace(R.id.content_main, fragment, MainActivity.VK_MESSAGES_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void clearData() {
            usersList.clear();
    }
}
