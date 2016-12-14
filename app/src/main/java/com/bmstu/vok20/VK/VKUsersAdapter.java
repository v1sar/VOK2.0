package com.bmstu.vok20.VK;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmstu.vok20.R;
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
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView first_name;
        private final TextView last_name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.first_name = (TextView) itemView.findViewById(R.id.found_user_first_name);
            this.last_name = (TextView) itemView.findViewById(R.id.found_user_last_name);
        }

    }

}
