package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.LoginPojo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PwModel {

    @POST("echo")
    Call<ResponseBody> loginUser(@Body LoginPojo user);
}
