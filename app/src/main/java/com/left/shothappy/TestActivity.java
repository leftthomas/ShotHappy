package com.left.shothappy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.left.shothappy.config.MyApplication;
import com.left.shothappy.utils.PicUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
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
    //用来记录正确的ImageView
    private ImageView trueImage;
    private ImageView to_game;
    private TextView word, number_text;
    private View view;
    private ArrayList<String> words;
    //用来设置选项的时候做随机
    private ArrayList<ImageView> images;
    //用来记录当前做到了第几题
    private int number;
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        words = intent.getStringArrayListExtra("words");
        //初始化为1
        number = 1;
        //只随机保留10个已学过的单词
        while (words.size() > 10) {
            words.remove(new Random().nextInt(words.size()));
        }

        view = findViewById(R.id.test_view);
        word = (TextView) findViewById(R.id.word);
        number_text = (TextView) findViewById(R.id.number);
        word.setTypeface(MyApplication.typeFace);
        number_text.setTypeface(MyApplication.typeFace);
        number_text.setText(number + "/10");
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

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap.put(1, soundPool.load(this, R.raw.excellent, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.great, 2));
        soundPoolMap.put(3, soundPool.load(this, R.raw.good, 3));
        soundPoolMap.put(4, soundPool.load(this, R.raw.perfect, 4));
        //最后一个是选错的提示音
        soundPoolMap.put(5, soundPool.load(this, R.raw.tryagain, 5));

        //默认没有哪张图被选中
        currentImage = null;
        trueImage = null;
        //设置第一个单词的选项
        setAllView(words.get(0));

        to_game = (ImageView) findViewById(R.id.next);
        to_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //做下判断,看看是否做完了10道测试题,记住，一定是11
                if (number == 11) {
                    startActivity(new Intent(getApplicationContext(), GameActivity.class));
                } else {
                    Snackbar.make(view, R.string.game_tip, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        handleImageView((ImageView) v);
        if (currentImage == trueImage) {
            //还没做满10道题目
            if (number < 10) {
                //发声音
                playSound(new Random().nextInt(soundPoolMap.size() - 1) + 1, 0);
                //当前选中题置空
                currentImage.setImageDrawable(null);
                currentImage = null;
                //换下一道题
                setAllView(words.get(number));
                number++;
                //重新设置下number值
                number_text.setText(number + "/10");
            } else if (number == 10) {
                //发声音
                playSound(new Random().nextInt(soundPoolMap.size() - 1) + 1, 0);
                number++;
                Snackbar.make(view, R.string.game_to, Snackbar.LENGTH_SHORT).show();
            } else {
                //也就是number＝11的情况
                Snackbar.make(view, R.string.game_to, Snackbar.LENGTH_SHORT).show();
            }

        } else {
            //没选对，发出try again的声音
            playSound(5, 0);
        }
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

        ArrayList<ImageView> temps = new ArrayList<>(images);
        word.setText(name);
        Bitmap bmp = PicUtils.getLocalBitmapByAssets(this, getString(R.string.animals) + "/" + name.toLowerCase().replace(" ", "") + ".jpg");
        if (bmp != null) {
            //如果返回的bmp不为空，表示存在这个资源
            int index = new Random().nextInt(temps.size());
            temps.get(index).setBackgroundDrawable(new BitmapDrawable(bmp));
            //记得把正确的image记录下
            trueImage = images.get(index);
            temps.remove(index);
            List<String> imgs = getImageNamesList(getString(R.string.animals), name);
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
            bmp = PicUtils.getLocalBitmapByAssets(this, getString(R.string.fruits) + "/" + name.toLowerCase().replace(" ", "") + ".jpg");
            if (bmp != null) {
                //如果返回的bmp不为空，表示存在这个资源
                int index = new Random().nextInt(temps.size());
                temps.get(index).setBackgroundDrawable(new BitmapDrawable(bmp));
                trueImage = images.get(index);
                temps.remove(index);
                List<String> imgs = getImageNamesList(getString(R.string.fruits), name);
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
                bmp = PicUtils.getLocalBitmapByAssets(this, getString(R.string.vegetables) + "/" + name.toLowerCase().replace(" ", "") + ".jpg");
                if (bmp != null) {
                    //如果返回的bmp不为空，表示存在这个资源
                    int index = new Random().nextInt(temps.size());
                    temps.get(index).setBackgroundDrawable(new BitmapDrawable(bmp));
                    trueImage = images.get(index);
                    temps.remove(index);
                    List<String> imgs = getImageNamesList(getString(R.string.vegetables), name);
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

    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
        //参数：1、Map中取值   2、当前音量     3、最大音量  4、优先级   5、重播次数   6、播放速度
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
