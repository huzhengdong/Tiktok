package com.bytedance.Tiktok;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.bytedance.Tiktok.model.Message;

import android.view.View;

import com.bytedance.Tiktok.model.MessageListResponse;
import com.bytedance.Tiktok.socket.SocketActivity;
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




public class MainActivity extends AppCompatActivity {
    private FeedAdapter adapter = new FeedAdapter();
    private List<Message> MessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(new FeedAdapter.OnClickListener() {
            @Override
            public void onClick(String videoUrl) {
                Intent playVideo = new Intent(MainActivity.this, videoPlay.class);
                playVideo.putExtra("videoUrl", videoUrl);
                startActivity(playVideo);
            }
        }
        );





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
        findViewById(R.id.btn_socket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SocketActivity.class);
                startActivity(intent);
            }
        });



    }

    //TODO 2
    // 用HttpUrlConnection实现获取留言列表数据，用Gson解析数据，更新UI（调用adapter.setData()方法）
    // 注意网络请求和UI更新分别应该放在哪个线程中
    private void getData(String studentId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                MessageList = baseGetMessageFromRemote(
                    studentId, "application/json");
            }
        }).start();
        adapter.setData(MessageList);
    }

    public List<Message> baseGetMessageFromRemote(String userName, String accept) {
        String urlStr =
                String.format("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/video?student_id=%s", userName);

        if(userName==null) {
            urlStr = String.format("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/video");
        }

        MessageListResponse result = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);

            conn.setRequestMethod("GET");

            conn.setRequestProperty("accept", accept);

            if (conn.getResponseCode() == 200) {

                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

                result = new Gson().fromJson(reader, new TypeToken<MessageListResponse>() {
                }.getType());

                reader.close();
                in.close();

            } else {
                // 错误处理
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "网络异常" + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return result.feeds;
    }


}