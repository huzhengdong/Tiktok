package com.SJTU7.Tiktok;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.SJTU7.Tiktok.VideoItem;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.SJTU7.Tiktok.VideoItemListResponse;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    private FeedAdapter adapter = new FeedAdapter();
    private List<VideoItem> VideoList;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animationView = findViewById(R.id.animation_view);
        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getData(null);
        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UploadActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.btn_mine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(Constants.STUDENT_ID);
            }
        });

        findViewById(R.id.btn_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(null);
            }
        });
        findViewById(R.id.btn_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomCameraActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData(String studentId){
        final VideoItemListResponse[] response = new VideoItemListResponse[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                response[0] =  baseGetMessageFromRemote(
                        studentId, "application/json");
            }
        }).start();
        //UI必须在主线程更新 而请求时间可能略长，大于setData的时间，导致更新UI完成时data并没有更新
        //因此连续点击两下才会有反应
        //解决方法：延迟更新UI 考虑到用户体验加上进度条
        recyclerView.setVisibility(View.GONE);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        getWindow().getDecorView().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                animationView.setVisibility(View.GONE);
                animationView.pauseAnimation();
                if(response[0].success)
                {
                    VideoList = response[0].feeds;
                    adapter.setData(VideoList);
                }

                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    public VideoItemListResponse baseGetMessageFromRemote(String userName, String accept) {
        String urlStr =
                String.format("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/video?student_id=%s", userName);

        if(userName==null) {
            urlStr = String.format("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/video");
        }

        VideoItemListResponse result = null;
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(6000);

            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", accept);

            if (conn.getResponseCode() == 200) {

                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

                result = new Gson().fromJson(reader, new TypeToken<VideoItemListResponse>() {
                }.getType());
                result.success = true;
                reader.close();
                in.close();

            } else {
                // 错误处理
                Log.e("cuowu","cuowu");
            }
            conn.disconnect();

        }  catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        if(result==null)
        {
            result = new VideoItemListResponse();
        }
        return result;
    }
}