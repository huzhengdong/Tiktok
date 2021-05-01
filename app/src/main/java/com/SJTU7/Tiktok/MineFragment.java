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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.SJTU7.Tiktok.Constants;

public class MineFragment extends Fragment
{
    private FeedAdapter adapter = new FeedAdapter();
    private List<VideoItem> VideoList;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private TextView textName;
    private EditText editName;
    private Button btn;
    private TextView user_id;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        //Fresco.initialize(getContext());
        animationView = view.findViewById(R.id.animation_view);
        recyclerView = view.findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        textName = view.findViewById(R.id.text_name);
        textName.setText(Constants.USER_NAME);
        editName = view.findViewById(R.id.edit_name);
        editName.setText(Constants.USER_NAME);
        user_id = view.findViewById(R.id.user_id);
        user_id.setText(Constants.USER_ID);
        btn = view.findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btn.getText().equals("修改昵称"))
                {
                    textName.setVisibility(View.GONE);
                    editName.setVisibility(View.VISIBLE);
                    btn.setText("确认");
                }
                else {
                    name = editName.getText().toString();
                    if(name.length()>0)
                    {
                        textName.setText(name);
                        textName.setVisibility(View.VISIBLE);
                        editName.setVisibility(View.GONE);
                        btn.setText("修改昵称");
                        Constants.USER_NAME = name;
                        Log.e("cname",Constants.USER_NAME);
                    }
                    else {
                        Toast.makeText(getContext(),"昵称禁止为空",Toast.LENGTH_SHORT).show();
                        }

                }
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData(Constants.USER_ID);
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
        getView().postDelayed(new Runnable() {
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

