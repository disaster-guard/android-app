package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.UserId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Model used for retrieving user Id.
  */
public interface IdModel {
    @GET("api/getUserId")
    Call<UserId> retrieveId(@Header("Authorization") String header, @Query("email") String email);
}
