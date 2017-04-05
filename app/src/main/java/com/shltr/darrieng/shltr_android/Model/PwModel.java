package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.LoginPojo;
import com.shltr.darrieng.shltr_android.Pojo.UserToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static com.shltr.darrieng.shltr_android.Activity.OnboardingActivity.BASE_URL;

public interface PwModel {
    String LOGIN_ENDPOINT = BASE_URL + "oauth/";

    @POST("token")
    Call<UserToken> loginUser(@Body LoginPojo user);
}
