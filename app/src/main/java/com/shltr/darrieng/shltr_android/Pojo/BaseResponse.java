package com.shltr.darrieng.shltr_android.Pojo;

/**
 * Base response when we only need a simple callback from RESTful API.
 */
public class BaseResponse {
    String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
