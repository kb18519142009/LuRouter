package com.example.bai.utils.utils;

public class JSCallbackWrapper {
    private String callback;
    private String jsonData;

    private JSCallbackWrapper() {
    }

    public static JSCallbackWrapper create(String callback, String jsonData) {
        JSCallbackWrapper jsCallback = new JSCallbackWrapper();

        jsCallback.callback = callback;
        jsCallback.jsonData = jsonData;

        return jsCallback;
    }

    public String stringify() {
        return "javascript:" + callback + "(" + jsonData + ")";
    }
}
