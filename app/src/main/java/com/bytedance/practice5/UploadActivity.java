package com.bytedance.practice5;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bytedance.practice5.model.MessageListResponse;
import com.bytedance.practice5.model.UploadResponse;
import com.bytedance.practice5.socket.SocketActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity {
    private static final String TAG = "chapter5";
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;
    private static final int REQUEST_CODE_COVER_IMAGE = 101;
    private static final int REQUEST_CODE_VIDEO = 202;
    private static final String COVER_IMAGE_TYPE = "image/*";
    private static final String VIDEO_TYPE = "video/*";

    private static final int MSG_START_COMPRESS = 0;
    private static final int MSG_END_COMPRESS = 1;

    private Retrofit retrofit;
//    private IApi api;
    private IApi service;
    private Uri coverImageUri;
    private Uri videoUri;
    public String compressPath;
    public Boolean flag = false;

    private SimpleDraweeView coverSD;
    private SimpleDraweeView videoSD;
    private EditText extraContentEditText;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNetwork();
        setContentView(R.layout.activity_upload);
        coverSD = findViewById(R.id.sd_cover);
        videoSD = findViewById(R.id.sd_video);
        animationView = findViewById(R.id.animation_view);

        extraContentEditText = findViewById(R.id.et_extra_content);

        findViewById(R.id.btn_cover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_COVER_IMAGE, COVER_IMAGE_TYPE, "选择图片");
            }
        });

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile(REQUEST_CODE_VIDEO, VIDEO_TYPE, "选择视频");
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_COVER_IMAGE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                coverImageUri = data.getData();
                coverSD.setImageURI(coverImageUri);

                if (coverImageUri != null) {
                    Log.d(TAG, "pick cover image " + coverImageUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

            } else {
                Log.d(TAG, "file pick fail");
            }
        }

        // TODO NEW
        //  获取和展示视频的Uri
        if (REQUEST_CODE_VIDEO == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                videoUri = data.getData();
                videoSD.setImageURI(videoUri);

                if (videoUri != null) {
                    Log.d(TAG, "pick video - videoUri: " + videoUri.toString());
                } else {
                    Log.d(TAG, "uri2File fail " + data.getData());
                }

                new Thread(){
                    @Override
                    public void run(){
                        super.run();
                        try{
                            /**
                             * 视频压缩
                             * 第一个参数：视频源文件路径 Uri
                             * 第二个参数：压缩后视频保存的路径
                             * Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) 是系统提供的公共目录
                             */
                            String systemPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
                            Log.d(TAG, "systemPath: "+ systemPath);
                            mHandler.sendMessage(Message.obtain(mHandler, MSG_START_COMPRESS));

                            compressPath = SiliCompressor.with(UploadActivity.this).compressVideo(videoUri, systemPath);
                            Log.d(TAG, "compressPath: "+ compressPath);
                            mHandler.sendMessage(Message.obtain(mHandler, MSG_END_COMPRESS));

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        System.out.println("压缩完毕");
                        flag=true;
                    }
                }.start();

            } else {
                Log.d(TAG, "file pick fail");
            }
        }
    }

    // TODO
    //  根据压缩状况修改界面
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(UploadActivity.this, "正在压缩，请稍后", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.btn_submit).setEnabled(false);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.getBackground().setAlpha(180);
                    animationView.bringToFront();
                    animationView.playAnimation();
                    break;
                case 1:
                    Toast.makeText(UploadActivity.this, "压缩完毕", Toast.LENGTH_SHORT).show();
                    findViewById(R.id.btn_submit).setEnabled(true);
                    animationView.setVisibility(View.INVISIBLE);
                    animationView.pauseAnimation();
                    break;
            }
            return false;
        }
    });

    private void initNetwork() {
        //TODO 3
        // 创建Retrofit实例
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api-sjtu-camp-2021.bytedance.com/homework/invoke/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 生成api对象
        service = retrofit.create(IApi.class);
    }

    private void getFile(int requestCode, String type, String title) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_TITLE, title);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    // TODO NEW
    //  提交时的注意事项
    private void submit() {

        // TODO
        //  获取图片信息
        byte[] coverImageData = readDataFromUri(coverImageUri);

        // TODO
        //  获取视频信息
        //  将 path 转换为 Uri

        videoUri = Uri.parse("file://"+compressPath);
        Log.d(TAG, "submit - videoUri: " + videoUri);
        byte[] videoData = readDataFromUri(videoUri);


        // TODO
        //  关于封面和视频信息的判断
        if (coverImageData == null || coverImageData.length == 0) {
            Toast.makeText(this, "封面不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        if (videoData == null || videoData.length == 0) {
            Toast.makeText(this, "视频为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = extraContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入视频备注", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( coverImageData.length + videoData.length >= MAX_FILE_SIZE) {
            Toast.makeText(this, "文件过大", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 5
        // 使用api.submitMessage()方法提交留言
        // 如果提交成功则关闭activity，否则弹出toast

        // TODO NEW
        //  更改 Body 中的内容，来自前端界面
        MultipartBody.Part image_part = MultipartBody.Part.createFormData(
                "cover_image",
                "cover.png",
                RequestBody.create(MediaType.parse("multipart/form_data"), coverImageData)
        );
        MultipartBody.Part video_part = MultipartBody.Part.createFormData(
                "video",
                "video_file.mp4",
                RequestBody.create(MediaType.parse("multipart/form_data"), videoData)
        );
        // TODO NEW
        //  更改 Call 的组成，应该和 submit 所需的网址要求相符合
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<UploadResponse> call = service.submitMessage(
                        Constants.STUDENT_ID,
                        Constants.USER_NAME,
                        content,
                        image_part,
                        video_part);
                try {
                    Response<UploadResponse> response = call.execute();
                    if (response.isSuccessful() && response.body().success){
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("upload", "run: back");
                                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private byte[] readDataFromUri(Uri uri) {
        byte[] data = null;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            data = Util.inputStream2bytes(is);
            Log.d(TAG, "readDataFromUri: here");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
