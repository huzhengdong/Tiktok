package com.SJTU7.Tiktok;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FriendFragment extends Fragment
{
    private FeedAdapter adapter = new FeedAdapter();
    private List<VideoItem> VideoList;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Fresco.initialize(getContext());
        animationView = view.findViewById(R.id.animation_view);
        recyclerView = view.findViewById(R.id.rv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        //Constants.friend_id.add("119082910012");


        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData(Constants.friend_id);
    }

    private void getData(List<String> friend_id){
        final VideoItemListResponse[] response = new VideoItemListResponse[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                response[0] =  baseGetMessageFromRemote(
                        null, "application/json");
            }
        }).start();
        //UI必须在主线程更新 而请求时间可能略长，大于setData的时间，导致更新UI完成时data并没有更新
        //因此连续点击两下才会有反应
        //解决方法：延迟更新UI 考虑到用户体验加上进度条
        recyclerView.setVisibility(View.GONE);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        getView().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                animationView.setVisibility(View.GONE);
                animationView.pauseAnimation();
                if(response[0]!=null)
                {
                    VideoList = response[0].feeds;
                    for(int i = VideoList.size() - 1; i >= 0; i--){
                        VideoItem item = VideoList.get(i);
                        if(!friend_id.contains(item.getStudentId())){
                            VideoList.remove(item);
                        }
                    }
                    adapter.setData(VideoList);
                }

                else {
                    Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
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
        return result;
    }
}
