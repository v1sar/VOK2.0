package com.bmstu.vok20.VK;

/**
 * Created by qwerty on 14.12.16.
 */

public class VKUsers {
    private String first_name;
    private String last_name;
    private int id;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {

        return url;
    }

    private String url;

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {

        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public int getId() {
        return id;
    }

    public VKUsers(String first_name, String last_name, int id, String url) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.id = id;
        this.url = url;
    }
}
