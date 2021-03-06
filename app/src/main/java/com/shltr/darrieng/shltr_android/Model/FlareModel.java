package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.BaseResponse;
import com.shltr.darrieng.shltr_android.Pojo.FlarePojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Model used to send flare to server.
 */
public interface FlareModel {
    @POST("api/flare")
    Call<BaseResponse> createUser(@Header("Authorization") String header, @Body FlarePojo flare);
}
