package com.bmstu.vok20.VK;

/**
 * Created by anthony on 03.11.16.
 */

public class VKMessage {
    private String body;
    private Boolean isOut;

    public VKMessage(String body, Boolean isOut) {
        this.body = body;
        this.isOut = isOut;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean isOut() {
        return isOut;
    }

    public void setIs_out(Boolean isOut) {
        this.isOut = isOut;
    }
}
