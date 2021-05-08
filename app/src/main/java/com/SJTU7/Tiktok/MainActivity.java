package com.SJTU7.Tiktok;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.viewpager.widget.ViewPager;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;


import android.view.View;

import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private FeedAdapter adapter = new FeedAdapter();
    private HomeFragment homeFragment;
    private FriendFragment friendFragment;
    private MineFragment mineFragment;
    private SharedPreferences spdata;
    private SharedPreferences.Editor editor;
    private String user_id;
    private TextView btn_home;
    private TextView btn_record;
    private TextView btn_upload;
    private TextView btn_mine;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                spdata = getSharedPreferences("data",MODE_PRIVATE);
                editor = spdata.edit();
//                editor.clear();
//                editor.apply();
                user_id = spdata.getString("id","-1");

                if(user_id.equals("-1"))
                {
                    user_id = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    try {
                        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                        int rand = random.nextInt(999);
                        String b = String.format("%03d",rand);
                        user_id+=b;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    Constants.USER_ID = user_id;
                    editor.putString("id",user_id);
                    editor.putString("name",Constants.USER_NAME);
                    editor.putInt("friend_amount",0);
                    editor.apply();
                }
                else {
                    Constants.USER_ID = user_id;
                    Constants.USER_NAME = spdata.getString("name","#");
                    int size = spdata.getInt("friend_amount",-1);
                    for(int i = 0;i < size;i++)
                    {
                        String id = spdata.getString("friend"+i,"");
                        if(!id.equals("")&&!Constants.friend_id.contains(id))
                        {
                            Constants.friend_id.add(id);
                        }
                    }
                }
            }
        }).start();
        setMenu();

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
                        friendFragment = new FriendFragment();
                        return friendFragment;
                    case 2:
                        mineFragment = new MineFragment();
                        return mineFragment;
                }
                return new HomeFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "所有";
                    case 1:
                        return "关注";
                    case 2:
                        return "我的";
                }
                return "首页";
            }
        });
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onPause() {//写在onDestroy会导致没有执行完程序已经结束了 onStop也不行 可能是单例模式的问题
        super.onPause();
        editor.putInt("friend_amount",Constants.friend_id.size());
        for(int i = 0;i < Constants.friend_id.size();i++)
        {
            editor.putString("friend"+i,Constants.friend_id.get(i));
        }
        editor.apply();
    }

    @SuppressLint("ResourceAsColor")
    public void setMenu()
    {
        btn_home = findViewById(R.id.btn_home);
        btn_record = findViewById(R.id.btn_record);
        btn_upload = findViewById(R.id.btn_upload);
        btn_mine = findViewById(R.id.btn_mine);
        btn_home.setTextColor(Color.WHITE);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CustomCameraActivity.class));
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UploadActivity.class));
            }
        });
        btn_mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MineActivity.class));
            }
        });
    }


}
