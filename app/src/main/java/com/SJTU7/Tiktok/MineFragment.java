package com.SJTU7.Tiktok;


import android.annotation.SuppressLint;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MineFragment extends Fragment
{
    private FeedAdapter adapter = new FeedAdapter();
    private List<VideoItem> VideoList;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwip;



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

        mSwip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        mSwip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                mSwip.setRefreshing(false);
            }
        });


        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    private void getData(){
        final VideoItemListResponse[] response = new VideoItemListResponse[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                response[0] =  baseGetMessageFromRemote(
                        Constants.USER_ID, "application/json");
            }
        }).start();
        //UI???????????????????????? ????????????????????????????????????setData????????????????????????UI?????????data???????????????
        //???????????????????????????????????????
        //???????????????????????????UI ????????????????????????????????????
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
                    adapter.setData(VideoList);
                }
                else {
                    Toast.makeText(getContext(),"????????????",Toast.LENGTH_SHORT).show();
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
                // ????????????
                Log.e("cuowu","cuowu");
            }
            conn.disconnect();

        }  catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "????????????" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}

