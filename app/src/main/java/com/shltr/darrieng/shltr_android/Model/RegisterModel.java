package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.RegisterPojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Model used for user registration.
 */
public interface RegisterModel {
    @POST("register")
    Call<Void> createUser(@Body RegisterPojo user);
}
