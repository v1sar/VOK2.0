package com.bmstu.vok20.VK;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bmstu.vok20.R;

import java.util.ArrayList;

/**
 * Created by anthony on 03.11.16.
 */

public class VKMessagesAdapter extends BaseAdapter {
    private final static String TAG = VKMessagesAdapter.class.getSimpleName();

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<VKMessage> messages;

    public VKMessagesAdapter(Context context, ArrayList<VKMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.layoutInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.vk_message_item, parent, false);
        }

        VKMessage message = getMessage(position);

        TextView messageView = (TextView) view.findViewById(R.id.vkMessageBody);
        messageView.setText(message.getBody());

        LinearLayout.LayoutParams leftGravityParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT
        );
        if (message.isOut()) {
            messageView.setBackground(ContextCompat.getDrawable(context, R.drawable.vk_message_out_bg));
            leftGravityParams.weight = 1.0f;
            leftGravityParams.gravity = Gravity.RIGHT;
            messageView.setLayoutParams(leftGravityParams);
        } else {
            messageView.setBackground(ContextCompat.getDrawable(context, R.drawable.vk_message_in_bg));
            leftGravityParams.weight = 1.0f;
            leftGravityParams.gravity = Gravity.LEFT;
            messageView.setLayoutParams(leftGravityParams);
        }

        return view;
    }

    private VKMessage getMessage(int position) {
        return (VKMessage) getItem(position);
    }
}
