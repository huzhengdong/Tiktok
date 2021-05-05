package com.SJTU7.Tiktok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private SharedPreferences spdata;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        setMenu();
        textName = findViewById(R.id.text_name);
        textName.setText(Constants.USER_NAME);
        editName = findViewById(R.id.edit_name);
        editName.setText(Constants.USER_NAME);
        user_id = findViewById(R.id.user_id);
        user_id.setText(Constants.USER_ID);
        btn = findViewById(R.id.btn);

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
    protected void onStop() {//写在onDestroy会导致没有执行完程序已经结束了
        super.onStop();
        spdata = getSharedPreferences("data",MODE_PRIVATE);
        editor = spdata.edit();
        editor.putString("name",Constants.USER_NAME);
        editor.apply();
    }

    public void setMenu()
    {
        btn_home = findViewById(R.id.btn_home);
        btn_record = findViewById(R.id.btn_record);
        btn_upload = findViewById(R.id.btn_upload);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MineActivity.this,MainActivity.class));
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
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