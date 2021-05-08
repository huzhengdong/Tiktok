package com.SJTU7.Tiktok;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IApi {

    @Multipart
    @POST("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/video")
    Call<UploadResponse> submitMessage(@Query("student_id") String studentId,
                                     @Query("user_name") String userName,
                                     @Query("extra_value") String extraValue,
                                     @Part MultipartBody.Part coverImage,
                                     @Part MultipartBody.Part video
                                    );
}
