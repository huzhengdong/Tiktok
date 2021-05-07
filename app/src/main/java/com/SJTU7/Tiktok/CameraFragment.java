package com.SJTU7.Tiktok;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CameraFragment extends Fragment implements SurfaceHolder.Callback {

    private final static int PERMISSION_REQUEST_CODE = 1001;
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private SurfaceHolder mHolder;
    //    private ImageView mImageView;
    private VideoView mVideoView;
    private TextView mTest;
    private Button mRecordButton;
    private boolean isRecording = false;
    private int timeing = 0;
    public Handler handler = new Handler();
    private String mp4Path = "";
    private ProgressBar progressBar;
    private ImageButton btn_yes;
    private ImageButton btn_no;

    public static void startUI(Context context) {
        Intent intent = new Intent(context, CustomCameraActivity.class);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mSurfaceView = view.findViewById(R.id.surfaceview);
        mVideoView = view.findViewById(R.id.videoview);
        mRecordButton = view.findViewById(R.id.bt_record);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record(v);
            }
        });

//        mTest = findViewById(R.id.rest_time);
        mHolder = mSurfaceView.getHolder();
        try
        {
            requestPermission();
            initCamera();
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(),"相机初始化失败",Toast.LENGTH_SHORT).show();
        }

        mHolder.addCallback(this);
        progressBar = view.findViewById(R.id.progressbar);
        progressBar.setMax(1000);
        progressBar.setMin(0);
        btn_yes = view.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressYes(v);
            }
        });
        btn_no = view.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressNo(v);
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            if( isRecording && timeing < 1000 ){
                progressBar.setVisibility(View.VISIBLE);
                timeing++;
//                mTest.setText("recording");
                progressBar.setProgress((1000 - timeing));
                handler.postDelayed(this, 1);
            }
            else if(isRecording && timeing >= 1000){
//                mTest.setText("record stop");
                mRecordButton.callOnClick();
                progressBar.setVisibility(View.INVISIBLE);

            }
            else{
                handler.removeCallbacks(this);
            }

        }
    };

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {

        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(getActivity(), permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void initCamera() {
        mCamera = Camera.open();
//        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.setPictureFormat(ImageFormat.JPEG);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        parameters.set("orientation", "portrait");
//        parameters.set("rotation", 90);
//        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);

    }

    private boolean prepareVideoRecorder() {
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mp4Path = getOutputMediaPath();
        mMediaRecorder.setOutputFile(mp4Path);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setMaxDuration(10 * 1000);
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private String getOutputMediaPath() {
        File mediaStorageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".mp4");
        if (!mediaFile.exists()) {
            mediaFile.getParentFile().mkdirs();
        }
        return mediaFile.getAbsolutePath();
    }

    public void pressNo(View view){
        mVideoView.setVisibility(View.INVISIBLE);
        btn_yes.setVisibility(View.INVISIBLE);
        btn_no.setVisibility(View.INVISIBLE);
        mRecordButton.setVisibility(View.VISIBLE);
    }
    public void pressYes(View view){
        //Intent intent = new Intent(getContext(),UploadActivity.class);
        //startActivity(intent);
    }


    public void record(View view) {
        if (isRecording) {
            mRecordButton.setText("录制");

            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setOnInfoListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();

            mVideoView.setVisibility(View.VISIBLE);
            btn_yes.setVisibility(View.VISIBLE);
            btn_no.setVisibility(View.VISIBLE);
            mRecordButton.setVisibility(View.INVISIBLE);
//            mImageView.setVisibility(View.GONE);
            mVideoView.setVideoPath(mp4Path);
            mVideoView.start();
            progressBar.setVisibility(View.INVISIBLE);
//            mTest.setText("record stop");
        } else {
            if(prepareVideoRecorder()) {
                timeing = 0;
                handler.post(runnable);
                mRecordButton.setText("暂停");
                mMediaRecorder.start();
            }
        }
        isRecording = !isRecording;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        //停止预览效果
        mCamera.stopPreview();
        //重新设置预览效果
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            try {
                initCamera();
            }
            catch(Exception e)
            {
                Toast.makeText(getContext(),"相机初始化失败", Toast.LENGTH_SHORT).show();
            }

        }
        mCamera.startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }


}