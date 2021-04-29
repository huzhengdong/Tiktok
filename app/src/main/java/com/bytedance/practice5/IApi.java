package com.bytedance.practice5;


import com.bytedance.practice5.model.UploadResponse;
import com.bytedance.practice5.model.Message;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IApi {

    //TODO 4
    // 补全所有注解
    @Multipart
    @POST("video")
    Call<UploadResponse> submitMessage(@Query("student_id") String studentId,
                                     @Query("user_name") String userName,
                                     @Query("extra_value") String extraValue,
                                     @Part MultipartBody.Part cover_image,
                                     @Part MultipartBody.Part video);
}
