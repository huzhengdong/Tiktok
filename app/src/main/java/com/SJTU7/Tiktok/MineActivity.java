package com.SJTU7.Tiktok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MineActivity extends AppCompatActivity {
    private TextView textName;
    private EditText editName;
    private Button btn;
    private TextView user_id;
    private String name;
    private TextView btn_record;
    private TextView btn_upload;
    private TextView btn_home;
    private TextView btn_mine;

    private SharedPreferences spdata;
    private SharedPreferences.Editor editor;

    private RecyclerView recyclerView;
    private FriendAdapter adapter = new FriendAdapter();
    private EditText et_add;
    private Button btn_add;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        spdata = getSharedPreferences("data",MODE_PRIVATE);
        editor = spdata.edit();
        setMenu();
        textName = findViewById(R.id.text_name);
        textName.setText(Constants.USER_NAME);
        editName = findViewById(R.id.edit_name);
        editName.setText(Constants.USER_NAME);
        user_id = findViewById(R.id.user_id);
        user_id.setText(Constants.USER_ID);
        btn = findViewById(R.id.btn);
        recyclerView = findViewById(R.id.friend_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MineActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setData(Constants.friend_id);
        recyclerView.setAdapter(adapter);

        et_add= findViewById(R.id.et_add);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String add = et_add.getText().toString();
                //Toast.makeText(MineActivity.this,String.valueOf(Constants.all_id.size()),Toast.LENGTH_SHORT).show();
                if(Constants.friend_id.contains(add)){
                    Toast.makeText(MineActivity.this,"该用户已被关注",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(Constants.USER_ID.equals(add))
                {
                    Toast.makeText(MineActivity.this,"不能关注自己",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if(Constants.all_id.contains(add))
                {
                    Constants.friend_id.add(add);
                    adapter.setData(Constants.friend_id);
                    et_add.setText("");
                    return ;
                }
                Toast.makeText(MineActivity.this,"该用户不存在",Toast.LENGTH_SHORT).show();
                return;
            }
        });
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
                    if(name.length()>10)
                    {
                        Toast.makeText(MineActivity.this,"昵称过长",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(name.length()>0)
                    {
                        textName.setText(name);
                        textName.setVisibility(View.VISIBLE);
                        editName.setVisibility(View.GONE);
                        btn.setText("修改昵称");
                        Constants.USER_NAME = name;
                        InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        m.hideSoftInputFromWindow(editName.getWindowToken(), 0);
                    }
                    else {
                        Toast.makeText(MineActivity.this,"昵称禁止为空",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.setData(Constants.friend_id);
    }

    @Override
    protected void onPause() {//写在onDestroy会导致没有执行完程序已经结束了 onStop也不行 可能是单例模式的问题
        super.onPause();
        editor.putString("name",Constants.USER_NAME);
        editor.putInt("friend_amount",Constants.friend_id.size());
        for(int i = 0;i < Constants.friend_id.size();i++)
        {
            editor.putString("friend"+i,Constants.friend_id.get(i));
        }
        editor.apply();
    }

    public void setMenu()
    {
        btn_home = findViewById(R.id.btn_home);
        btn_record = findViewById(R.id.btn_record);
        btn_upload = findViewById(R.id.btn_upload);
        btn_mine = findViewById(R.id.btn_mine);
        btn_mine.setTextColor(Color.WHITE);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this,MainActivity.class));
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this,CustomCameraActivity.class));
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this,UploadActivity.class));
            }
        });
    }
}