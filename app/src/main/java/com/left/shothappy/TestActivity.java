package com.left.shothappy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.left.shothappy.utils.PicUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestActivity extends BaseActivity implements View.OnClickListener {

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
    private ArrayList<String> words;
    //用来设置选项的时候做随机
    private ArrayList<ImageView> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        words = intent.getStringArrayListExtra("words");
        //只随机保留10个已学过的单词
        while (words.size() > 10) {
            words.remove(new Random().nextInt(words.size()));
        }

        word = (TextView) findViewById(R.id.word);
        word.setTypeface(MainActivity.typeFace);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        images = new ArrayList<>();
        images.add(imageView1);
        images.add(imageView2);
        images.add(imageView3);
        images.add(imageView4);

        //默认没有哪张图被选中
        currentImage = null;
        //设置第一个单词的选项
        setAllView(words.get(0));

        next = (ImageView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                startActivity(new Intent(getApplicationContext(), GameActivity.class));
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

    /**
     * 根据当前单词设置界面上的选项情况与单词
     * 单词记得转小写，去空格
     *
     * @param name
     */
    private void setAllView(String name) {

        ArrayList<ImageView> temps = images;
        word.setText(name);
        Bitmap bmp = PicUtils.getLocalBitmapByAssets(this, "animals/" + name.toLowerCase().replace(" ", "") + ".jpg");
        if (bmp != null) {
            //如果返回的bmp不为空，表示存在这个资源
            int index = new Random().nextInt(temps.size());
            temps.get(index).setBackgroundDrawable(new BitmapDrawable(bmp));
            temps.remove(index);
            List<String> imgs = getImageNamesList("animals", name);
            while (temps.size() > 0) {
                //记录当前选中的图片位置
                int f = new Random().nextInt(imgs.size());
                Bitmap b = PicUtils.getLocalBitmapByAssets(this, imgs.get(f));
                int i = new Random().nextInt(temps.size());
                temps.get(i).setBackgroundDrawable(new BitmapDrawable(b));
                temps.remove(i);
                imgs.remove(f);
            }
        } else {
            bmp = PicUtils.getLocalBitmapByAssets(this, "fruits/" + name.toLowerCase().replace(" ", "") + ".jpg");
            if (bmp != null) {
                //如果返回的bmp不为空，表示存在这个资源
                int index = new Random().nextInt(temps.size());
                temps.get(index).setBackgroundDrawable(new BitmapDrawable(bmp));
                temps.remove(index);
                List<String> imgs = getImageNamesList("fruits", name);
                while (temps.size() > 0) {
                    //记录当前选中的图片位置
                    int f = new Random().nextInt(imgs.size());
                    Bitmap b = PicUtils.getLocalBitmapByAssets(this, imgs.get(f));
                    int i = new Random().nextInt(temps.size());
                    temps.get(i).setBackgroundDrawable(new BitmapDrawable(b));
                    temps.remove(i);
                    imgs.remove(f);
                }
            } else {
                bmp = PicUtils.getLocalBitmapByAssets(this, "vegetables/" + name.toLowerCase().replace(" ", "") + ".jpg");
                if (bmp != null) {
                    //如果返回的bmp不为空，表示存在这个资源
                    int index = new Random().nextInt(temps.size());
                    temps.get(index).setBackgroundDrawable(new BitmapDrawable(bmp));
                    temps.remove(index);
                    List<String> imgs = getImageNamesList("vegetables", name);
                    while (temps.size() > 0) {
                        //记录当前选中的图片位置
                        int f = new Random().nextInt(imgs.size());
                        Bitmap b = PicUtils.getLocalBitmapByAssets(this, imgs.get(f));
                        int i = new Random().nextInt(temps.size());
                        temps.get(i).setBackgroundDrawable(new BitmapDrawable(b));
                        temps.remove(i);
                        imgs.remove(f);
                    }
                }
            }
        }
    }


    /**
     * 根据单词所属类别获取本类别所有图片名称,要剔除当前单词本身
     * type要写全，例如“animals”
     *
     * @param type
     * @param name
     */
    private List<String> getImageNamesList(String type, String name) {
        List<String> imageNames = new ArrayList<>();
        InputStream listFile;
        try {
            listFile = getAssets().open(new File(type + ".txt").getPath());
            BufferedReader br = new BufferedReader(new InputStreamReader(listFile));
            String imageName;
            while (null != (imageName = br.readLine())) {
                //记得转小写＋去空格,判断是不是本单词
                if (!imageName.equals(name))
                    imageNames.add(type + "/" + imageName.toLowerCase().replace(" ", "") + ".jpg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageNames;
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
