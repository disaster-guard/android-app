package com.shltr.darrieng.shltr_android.Model;

import com.shltr.darrieng.shltr_android.Pojo.CompleteIdentificationModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * UNUSED: Model used for uploading images to the server.
 */
public interface UploaderServiceModel {
    @Multipart
    @POST("api/uploadPredictionPicture")
    Call<CompleteIdentificationModel> postImage(@Part MultipartBody.Part image, @Part("name") RequestBody name);
}
