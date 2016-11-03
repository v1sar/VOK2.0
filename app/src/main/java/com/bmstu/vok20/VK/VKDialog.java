package com.bmstu.vok20.VK;

/**
 * Created by anthony on 03.11.16.
 */

public class VKDialog {
    private String title;
    private String lastMessage;
    private String avatarUrl;
    private int userId;

    public VKDialog(int userId, String title, String lastMessage, String avatarUrl) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.avatarUrl = avatarUrl;
        this.userId = userId;
    }

    public VKDialog(int userId, String title, String lastMessage) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.avatarUrl = null;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
