package com.bmstu.vok20.VK;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by anthony on 03.11.16.
 */

@DatabaseTable(tableName = "vk_message")
public class VKMessage {

    public final static String VK_MESSAGE_SENDER_ID_FIELD_NAME = "sender_id";
    public final static String VK_MESSAGE_IS_OUT_FIELD_NAME = "is_out";
    public final static String VK_MESSAGE_BODY_FIELD_NAME = "body";
    public final static String VK_MESSAGE_TIMESTAMP_FIELD_NAME = "timestamp";

    @DatabaseField(generatedId = true)
    private static long id;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = VK_MESSAGE_SENDER_ID_FIELD_NAME)
    private int senderId;

    @DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN, columnName = VK_MESSAGE_IS_OUT_FIELD_NAME)
    private boolean isOut;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = VK_MESSAGE_BODY_FIELD_NAME)
    private String body;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = VK_MESSAGE_TIMESTAMP_FIELD_NAME)
    private long timestamp;

    public VKMessage() {}


    public VKMessage(int senderId, boolean isOut, String body, long timestamp) {
        this.senderId = senderId;
        this.isOut = isOut;
        this.body = body;
        this.timestamp = timestamp;
    }

    public static long getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", ").append("senderId=").append(senderId);
        sb.append(", ").append("isOut=").append(isOut);
        sb.append(", ").append("body=").append(body);
        sb.append(", ").append("timestamp=").append(timestamp);
        return sb.toString();
    }
}
