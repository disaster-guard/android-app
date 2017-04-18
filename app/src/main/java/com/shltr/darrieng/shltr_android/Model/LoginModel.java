package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.LoginPojo;
import com.shltr.darrieng.shltr_android.Pojo.UserToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginModel {

    @POST("oauth")
    Call<UserToken> loginUser(@Body LoginPojo user);
}
