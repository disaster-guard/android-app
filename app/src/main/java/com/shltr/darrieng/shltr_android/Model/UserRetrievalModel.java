package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.UserPojo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Model used for getting more information about users when they are nearby.
 */
public interface UserRetrievalModel {
    @GET("api/nearByProfile")
    Call<UserPojo> retrieveId(@Header("Authorization") String header, @Query("email") String email);
}
