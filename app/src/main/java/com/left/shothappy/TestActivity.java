package com.left.shothappy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    /**
     * 当前的ImageView
     */
    private ImageView currentImage;
    private ImageView next;
    private TextView word;
    private Typeface typeFace;
    private ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/bear-rabbit.ttf");
        Intent intent = getIntent();
        words = intent.getStringArrayListExtra("words");
        //只随机保留10个已学过的单词
        while (words.size() > 10) {
            words.remove(new Random().nextInt(words.size()));
        }

        word = (TextView) findViewById(R.id.word);
        word.setTypeface(typeFace);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);

        //设置第一个单词
        word.setText(words.get(0));
        //默认没有哪张图被选中
        currentImage = null;

        next = (ImageView) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                startActivity(new Intent(getApplicationContext(), GameActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    @Override
    public void onClick(View v) {
        handleImageView((ImageView) v);
    }

    /**
     * 给点击选中的ImageView加边框，并将之前的ImageView边框清除
     *
     * @param imageView 要添加边框的ImageView
     */
    public void handleImageView(ImageView imageView) {
        if (currentImage != null)
            currentImage.setImageDrawable(null);
        imageView.setImageResource(R.drawable.border);
        currentImage = imageView;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("ExitTest");
        this.registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }
}
