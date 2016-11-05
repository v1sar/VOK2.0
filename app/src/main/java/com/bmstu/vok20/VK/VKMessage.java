package com.bmstu.vok20.VK;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by anthony on 03.11.16.
 */

@DatabaseTable(tableName = "vk_message")
public class VKMessage {

    private final static String VK_MESSAGE_SENDER_ID_FIELD_NAME = "sender_id";
    private final static String VK_MESSAGE_RECEIVER_ID_FIELD_NAME = "receiver_id";
    private final static String VK_MESSAGE_IS_OUT_FIELD_NAME = "is_out";
    private final static String VK_MESSAGE_BODY_FIELD_NAME = "body";
    private final static String VK_MESSAGE_TIMESTAMP_FIELD_NAME = "timestamp";

    @DatabaseField(generatedId = true)
    private static long id;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = VK_MESSAGE_SENDER_ID_FIELD_NAME)
    private int senderId;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = VK_MESSAGE_RECEIVER_ID_FIELD_NAME)
    private int receiverId;

    @DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN, columnName = VK_MESSAGE_IS_OUT_FIELD_NAME)
    private boolean isOut;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = VK_MESSAGE_BODY_FIELD_NAME)
    private String body;

    @DatabaseField(canBeNull = false, dataType = DataType.TIME_STAMP, columnName = VK_MESSAGE_TIMESTAMP_FIELD_NAME)
    private int timestamp;

    public VKMessage() {}

    public VKMessage(int senderId, int receiverId, boolean isOut, String body, int timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
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

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
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

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
