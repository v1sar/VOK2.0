package com.bmstu.vok20.Helpers;

/**
 * Created by qwerty on 07.11.16.
 */

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Request;
import okhttp3.Response;


public class UrlDownloader {

    private static final UrlDownloader DOWNLOADER = new UrlDownloader();

    public static UrlDownloader getInstance() {
        return DOWNLOADER;
    }

    public interface Callback {
        void onLoaded(String request, String value);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void load(final String url) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String result;
                try {
                    result = loadInternal(url);
                } catch (IOException e) {
                    result = null;
                }
                notifyLoaded(url, result);
            }
        });
    }

    private void notifyLoaded(final String url, final String result) {
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onLoaded(url, result);
                }
            }
        });
    }

    private String loadInternal(String url) throws IOException {
        Response response =
            Http.getClient().newCall(
                new Request.Builder()
                            .url(url)
                            .build()
            ).execute();

        try {
            return response.body().string();
        } finally {
            response.close();
        }
    }
}
