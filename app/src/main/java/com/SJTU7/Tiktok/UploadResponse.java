package com.SJTU7.Tiktok;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("result")
    public VideoItem video;
    @SerializedName("success")
    public boolean success;
    @SerializedName("error")
    public String error;
}
