package com.bmstu.vok20.VK;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by anthony on 03.11.16.
 */

@DatabaseTable(tableName = "vk_message")
public class VKMessage {

    public final static String VK_MESSAGE_USER_ID_FIELD_NAME = "user_id";
    public final static String VK_MESSAGE_IS_OUT_FIELD_NAME = "is_out";
    public final static String VK_MESSAGE_BODY_FIELD_NAME = "body";
    public final static String VK_MESSAGE_TIMESTAMP_FIELD_NAME = "timestamp";

    @DatabaseField(generatedId = true)
    private static long id;

    @DatabaseField(uniqueIndexName = "user_body_ts_index", canBeNull = false, dataType = DataType.INTEGER, columnName = VK_MESSAGE_USER_ID_FIELD_NAME)
    private int userId;

    @DatabaseField(canBeNull = false, dataType = DataType.BOOLEAN, columnName = VK_MESSAGE_IS_OUT_FIELD_NAME)
    private boolean isOut;

    @DatabaseField(uniqueIndexName = "user_body_ts_index", canBeNull = false, dataType = DataType.STRING, columnName = VK_MESSAGE_BODY_FIELD_NAME)
    private String body;

    @DatabaseField(uniqueIndexName = "user_body_ts_index", canBeNull = false, dataType = DataType.LONG, columnName = VK_MESSAGE_TIMESTAMP_FIELD_NAME)
    private long timestamp;

    public VKMessage() {}

    public VKMessage(int userId, boolean isOut, String body, long timestamp) {
        this.userId = userId;
        this.isOut = isOut;
        this.body = body;
        this.timestamp = timestamp;
    }

    public VKMessage(String body, boolean isOut) {
        this.body = body;
        this.isOut = isOut;
    }

    public static long getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id=").append(id);
        stringBuilder.append(", ").append("userId=").append(userId);
        stringBuilder.append(", ").append("isOut=").append(isOut);
        stringBuilder.append(", ").append("body=").append(body);
        stringBuilder.append(", ").append("timestamp=").append(timestamp);
        return stringBuilder.toString();
    }
}
