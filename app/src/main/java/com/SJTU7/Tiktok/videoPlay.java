package com.SJTU7.Tiktok;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class videoPlay extends AppCompatActivity {
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable runnable;
    private TextView textViewTime;
    private TextView textViewCurrentPosition;
    private String videoUrl;
    private ImageButton play_stop;
    static boolean isPlay = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("MediaPlayer");
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();

        }

        setContentView(R.layout.activity_video_play);
        textViewCurrentPosition =findViewById(R.id.textViewCurrentPosition1);
        textViewTime =findViewById(R.id.textViewTime1);
        surfaceView = findViewById(R.id.surfaceView);
        seekBar = findViewById(R.id.seekbar);
        play_stop =findViewById(R.id.btn);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        textViewTime.setVisibility(View.INVISIBLE);
        textViewCurrentPosition.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        play_stop.setVisibility(View.INVISIBLE);
        play_stop.setColorFilter(Color.WHITE);
        play_stop.setImageResource(R.drawable.time_out);
        //监听"播放/暂停"按钮
        play_stop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(isPlay){
                    ((ImageButton)v).setImageResource(R.drawable.play_circle);
                    player.pause();

                }
                else{
                    ((ImageButton)v).setImageResource(R.drawable.time_out);
                    player.start();

                }
                isPlay = !isPlay;
            }
        });


        Intent intent =getIntent();
        String videoUrl =intent.getStringExtra("videoUrl");
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                try {
                    if (player.isPlaying()) {
                        int current = player.getCurrentPosition();
                        int duration = player.getDuration();
                        seekBar.setProgress(current * 100 / duration);
                        textViewTime.setText(time(player.getCurrentPosition()));
                        textViewCurrentPosition.setText(time(duration));
                    }
                }
                catch (IllegalStateException e)
                {
                    player =null;
                }
                catch (NullPointerException e)
                {
                    System.out.println("player is null");
                }
                handler.postDelayed(runnable,50);
            }
        };
        handler.post(runnable);


        player = new MediaPlayer();
        try {
            Uri path = Uri.parse(videoUrl);
            player.setDataSource(this, path);
            holder = surfaceView.getHolder();
            holder.setFormat(PixelFormat.TRANSPARENT);
            holder.addCallback(new PlayerCallBack());
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 自动播放
                    player.start();
                    player.setLooping(true);
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    System.out.println(percent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.start();
            }
        });
        findViewById(R.id.buttonPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
            }
        });*/


    }


    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        // 当进度条停止修改的时候触发
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 取得当前进度条的刻度
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            int duration = player.getDuration();
            progress = seekBar.getProgress();
            if (fromUser) {
                // 设置当前播放的位置
                player.seekTo(duration*progress/100);
            }

        }
    };
    protected String time(long millionSeconds) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private final Runnable hideSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            textViewTime.setVisibility(View.INVISIBLE);
            textViewCurrentPosition.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
            play_stop.setVisibility(View.INVISIBLE);
        }
    };

    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(hideSeekBarRunnable);
                textViewTime.setVisibility(View.VISIBLE);
                textViewCurrentPosition.setVisibility(View.VISIBLE);
                seekBar.setVisibility(View.VISIBLE);
                play_stop.setVisibility(View.VISIBLE);
                handler.postDelayed(hideSeekBarRunnable,3000);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }



}