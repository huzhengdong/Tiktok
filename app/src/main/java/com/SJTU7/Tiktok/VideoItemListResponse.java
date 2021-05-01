package com.SJTU7.Tiktok;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoItemListResponse {
    @SerializedName("feeds")
    public List<com.SJTU7.Tiktok.VideoItem> feeds;
    @SerializedName("success")
    public boolean success;

}
