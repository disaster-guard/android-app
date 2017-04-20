package com.shltr.darrieng.shltr_android.DebugUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class LogBug {
    public static String getRetroBody(Response<ResponseBody> response) {
        String msg;
        try {
            msg = response.body().string();
        } catch (Exception e) {
            msg = "Could not get message " + e;
        }
        return msg;
    }

    public static String getRetroCodeMsg(Response<?> response) {
        return "Error code: " + response.code() + ", Error message: " + response.message();
    }
}
