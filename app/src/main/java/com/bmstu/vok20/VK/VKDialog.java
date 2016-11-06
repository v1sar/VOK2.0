package com.bmstu.vok20.VK;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by anthony on 03.11.16.
 */

@DatabaseTable(tableName = "vk_dialog")
public class VKDialog {

    public static final String VK_DIALOG_USER_ID_FIELD_NAME = "user_id";
    public static final String VK_DIALOG_TITLE_FIELD_NAME = "title";
    public static final String VK_DIALOG_UNREAD_FIELD_NAME = "unread";
    public static final String VK_DIALOG_AVATAR_URL_FIELD_NAME = "avatar_url";
    public static final String VK_DIALOG_LAST_MESSAGE_BODY_FIELD_NAME = "last_message_body";
    public static final String VK_DIALOG_LAST_MESSAGE_TIMESTAMP_FIELD_NAME = "last_message_ts";

    private static final String VK_DIALOG_UNREAD_FIELD_DEFAULT_VALUE = "0";
    private static final String VK_DIALOG_AVATAR_PATH_FIELD_DEFAULT_VALUE = "";

    @DatabaseField(generatedId = true)
    private static long id;

    @DatabaseField(canBeNull = false, dataType = DataType.INTEGER, columnName = VK_DIALOG_USER_ID_FIELD_NAME)
    private int userId;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = VK_DIALOG_TITLE_FIELD_NAME)
    private String title;

    @DatabaseField(dataType = DataType.INTEGER, columnName = VK_DIALOG_UNREAD_FIELD_NAME, defaultValue = VK_DIALOG_UNREAD_FIELD_DEFAULT_VALUE)
    private int unread;

    @DatabaseField(dataType = DataType.STRING, columnName = VK_DIALOG_AVATAR_URL_FIELD_NAME, defaultValue = VK_DIALOG_AVATAR_PATH_FIELD_DEFAULT_VALUE)
    private String avatarUrl;

    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = VK_DIALOG_LAST_MESSAGE_BODY_FIELD_NAME)
    private String lastMessageBody;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = VK_DIALOG_LAST_MESSAGE_TIMESTAMP_FIELD_NAME)
    private long lastMessageTimestamp;

    public VKDialog() {}

    public VKDialog(int userId, String title, int unread,
                    String lastMessageBody, long lastMessageTimestamp) {
        this.userId = userId;
        this.title = title;
        this.unread = unread;
        this.avatarUrl = "";
        this.lastMessageBody = lastMessageBody;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLastMessageBody() {
        return lastMessageBody;
    }

    public void setLastMessageBody(String lastMessageBody) {
        this.lastMessageBody = lastMessageBody;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
