package com.example.writinglearner;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    private static String host = "http://106.52.184.19:443/";

    public static void sendGetRequest(String posturl, Map<String, String> headers, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(host + posturl)).newBuilder();
        if (!headers.isEmpty())
            for (String key : headers.keySet())
                httpBuilder.addQueryParameter(key, Objects.requireNonNull(headers.get(key)));

        Request request = new Request.Builder().url(httpBuilder.build()).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendPostRequest(String posturl, RequestBody body, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(host + posturl)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
