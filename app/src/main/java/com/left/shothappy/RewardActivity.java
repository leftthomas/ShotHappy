package com.left.shothappy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class RewardActivity extends AppCompatActivity {

    private VideoView video;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 设置屏幕常亮 *//* flag：标记 ； */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_reward);
        video = (VideoView) findViewById(R.id.reward_video);
        Intent intent = getIntent();
        //获取数据
        String name = intent.getStringExtra("name");
        String path = intent.getStringExtra("path");
        setTitle(name);

        uri = Uri.parse(path);
        video.setMediaController(new MediaController(this));
        video.setVideoURI(uri);
        video.start();
        video.requestFocus();
    }
}
