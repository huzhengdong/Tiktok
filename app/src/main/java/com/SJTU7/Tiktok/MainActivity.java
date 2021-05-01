package com.SJTU7.Tiktok;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.SJTU7.Tiktok.VideoItem;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.SJTU7.Tiktok.VideoItemListResponse;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    private FeedAdapter adapter = new FeedAdapter();
    private List<VideoItem> VideoList;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private HomeFragment homeFragment;
    private CameraFragment cameraFragment;
    private UploadFragment uploadFragment;
    private MineFragment mineFragment;
    private SharedPreferences spdata;
    private SharedPreferences.Editor editor;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spdata = getSharedPreferences("data",MODE_PRIVATE);
        editor = spdata.edit();
        user_id = spdata.getString("id","-1");
        if(user_id.equals("-1"))
        {
            user_id = String.valueOf(Calendar.getInstance().getTimeInMillis());
            Constants.USER_ID = user_id;
            editor.putString("id",user_id);
            editor.putString("name",Constants.USER_NAME);
            editor.apply();
        }
        else {
                Constants.USER_ID = user_id;
                Constants.USER_NAME = spdata.getString("name","#");
            }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        homeFragment = new HomeFragment();
                        return homeFragment;
                    case 1:
                        cameraFragment = new CameraFragment();
                        return cameraFragment;
                    case 2:
                        uploadFragment = new UploadFragment();
                        return uploadFragment;
                    case 3:
                        mineFragment = new MineFragment();
                        return mineFragment;
                }
                return new HomeFragment();
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "首页";
                    case 1:
                        return "录制";
                    case 2:
                        return "上传";
                    case 3:
                        return "我的";
                }
                return "首页";
            }
        });
        tabLayout.setupWithViewPager(pager);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //uploadFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {//写在onDestroy会导致没有执行完程序已经结束了
        super.onStop();
        editor.putString("name",Constants.USER_NAME);
        editor.apply();
    }

}
