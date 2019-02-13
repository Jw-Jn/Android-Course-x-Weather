package com.example.win.weather.helper;

/**
 * Created by win on 2016/6/11.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
