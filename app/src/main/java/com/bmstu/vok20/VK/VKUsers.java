package com.bmstu.vok20.VK;

/**
 * Created by qwerty on 14.12.16.
 */

public class VKUsers {
    private String first_name;
    private String last_name;

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {

        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public VKUsers(String first_name, String last_name) {

        this.first_name = first_name;
        this.last_name = last_name;
    }
}
