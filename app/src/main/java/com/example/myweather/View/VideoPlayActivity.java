package com.example.myweather.View;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.bumptech.glide.Glide;
import com.example.myweather.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static com.example.myweather.util.Load.makeStatusBarTransparent;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener {

    private JCVideoPlayerStandard player;

    private EditText url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent(VideoPlayActivity.this);
        setContentView(R.layout.activity_video_play);
        getWindow().getDecorView().setBackgroundResource(R.drawable.gray_backg);
        player = (JCVideoPlayerStandard) findViewById(R.id.player_list_video);
        player.setUp("",JCVideoPlayer.SCREEN_LAYOUT_LIST,"");
        url = findViewById(R.id.video_edit_text);
        findViewById(R.id.video_ok_button).setOnClickListener(this);
        findViewById(R.id.video_back_button).setOnClickListener(this);
    }

    private void play(){
        boolean setUp;
        setUp = player.setUp(url.getText()+"", JCVideoPlayer.SCREEN_LAYOUT_LIST, "");
        if (setUp) {
            Glide.with(VideoPlayActivity.this).load("http://a4.att.hudong.com/05/71/01300000057455120185716259013.jpg").into(player.thumbImageView);
        }
    }

    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_back_button:
                Intent intent = new Intent(VideoPlayActivity.this, WeatherActivity.class);
                startActivity(intent);
                break;
            case R.id.video_ok_button:
                play();
                break;
        }
    }
}