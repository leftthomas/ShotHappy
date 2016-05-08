package com.left.shothappy;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.left.shothappy.bean.Dict;
import com.left.shothappy.config.MyApplication;
import com.left.shothappy.utils.IcibaTranslate;
import com.left.shothappy.utils.PicUtils;
import com.left.shothappy.utils.Renderer;
import com.left.shothappy.utils.ScheduleUtils;
import com.left.shothappy.views.GLView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Random;

import cn.easyar.engine.EasyAR;

/**
 * AR认知的页面
 */
public class ARActivity extends AppCompatActivity {

    private static String key = "d5356f4fba54d722115519ad830267a5zb8JO6yVcvKqdMGZREIYgvtkTjIlmPiUibOw0ge9OsN5DjcVfrOJKpUGM1MwEavrkvcZuEVvKB78wbeIsscymKohIytJAzPWYRhcMlDD3q9oKr5uBTiVtUHizWuMpcxxo0LLtKRdwJE0rbTEnliocezla7mnTJXmN1PzwniC";
    private static View view, share_panel, speak_tip_view;

    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("ARNative");
    }

    private ImageView fab;
    private CardView cardView;
    private Dict dict;
    private AsyncPlayer player;
    private Typeface typeFace;
    /**
     * 接收到网络请求回复的数据之后通知UI更新
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("status");
            if (val.equals("true")) {
                // UI界面的更新等相关操作
                String text = dict.getKey() + "    " + dict.getPos_acceptations().get(0).getPos() + "    " +
                        dict.getPos_acceptations().get(0).getAcceptation();

                // 播放发音
                String path = dict.getPs_prons().get(0).getPron();
                Uri uri = Uri.parse(path);
                player.play(getApplicationContext(), uri, false, AudioManager.STREAM_MUSIC);
                Snackbar.make(view, text, Snackbar.LENGTH_LONG).setActionTextColor(Color.GREEN)
                        .setAction(">>>>>>", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setCard(dict);
                                speak_tip_view.setVisibility(View.INVISIBLE);
                                cardView.setVisibility(View.VISIBLE);
                            }
                        }).show();
                //记得去更新Schedule
                ScheduleUtils.UpdateSchedule(ARActivity.this, dict.getKey());
                speak_tip_view.setVisibility(View.INVISIBLE);
                follow_speak_view.setVisibility(View.VISIBLE);
            } else {
                // UI界面的更新等相关操作
                cardView.setVisibility(View.INVISIBLE);
                follow_speak_view.setVisibility(View.INVISIBLE);
                speak_tip_view.setVisibility(View.INVISIBLE);
                Snackbar.make(view, val, Snackbar.LENGTH_SHORT).show();
            }

        }
    };
    /**
     * 网络操作相关的子线程
     * 调用语音sdk与英文释义部分的网络请求
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();
            Bundle data = new Bundle();

            String source = nativeGetWord();

            if (source == null || source.equals("")) {
                data.putString("status", "请对准要识别的物体");
            } else {
                try {
                    dict = IcibaTranslate.translate(source);
                    data.putString("status", "true");//表示请求成功
                } catch (Exception e) {
                    e.printStackTrace();
                    data.putString("status", "翻译失败，请检查网络");
                }
            }
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap = new HashMap<>();
    private ARActivity activity;
    private ImageView share, back, close, share_wechatmoments, speak_over,
            share_wechat, share_qzone, share_qq, share_weibo, follow_speak_view;

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    //用来更新c层的rewards
    public static native void nativeUpdateRewards(String[] rewards);

    private native boolean nativeInit();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    //获取单词
    private native String nativeGetWord();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_ar);

        typeFace = Typeface.createFromAsset(getAssets(), "fonts/bear-rabbit.ttf");

        EasyAR.initialize(this, key);
        nativeInit();
        //记得一定要去调用这个方法，不然视频没法放
        ARActivity.nativeUpdateRewards(((MyApplication) getApplication()).getRewards());
        GLView glView = new GLView(this);
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);

        player = new AsyncPlayer("audio");
        fab = (ImageView) findViewById(R.id.fab);
        cardView = (CardView) findViewById(R.id.cardview);
        view = findViewById(R.id.ar_frameLayout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 开启一个子线程，进行翻译与语音API请求，注意，不能直接在主线程操作
                new Thread(networkTask).start();
            }
        });

        back = (ImageView) findViewById(R.id.back);
        share = (ImageView) findViewById(R.id.share);

        share_qq = (ImageView) findViewById(R.id.share_qq);
        share_qzone = (ImageView) findViewById(R.id.share_qzone);
        share_wechat = (ImageView) findViewById(R.id.share_wechat);
        share_wechatmoments = (ImageView) findViewById(R.id.share_wechatmoments);
        share_weibo = (ImageView) findViewById(R.id.share_weibo);
        close = (ImageView) findViewById(R.id.close_share);
        share_panel = findViewById(R.id.share_panel);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_panel.setVisibility(View.VISIBLE);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_panel.setVisibility(View.INVISIBLE);
            }
        });
        share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.QQ);
                share_panel.setVisibility(View.INVISIBLE);
            }
        });
        share_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.WEIXIN);
                share_panel.setVisibility(View.INVISIBLE);
            }
        });
        share_qzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.QZONE);
                share_panel.setVisibility(View.INVISIBLE);
            }
        });
        share_wechatmoments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.WEIXIN_CIRCLE);
                share_panel.setVisibility(View.INVISIBLE);
            }
        });
        share_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.SINA);
                share_panel.setVisibility(View.INVISIBLE);
            }
        });
        follow_speak_view = (ImageView) findViewById(R.id.follow_speak_view);
        follow_speak_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow_speak_view.setVisibility(View.INVISIBLE);
                speak_tip_view.setVisibility(View.VISIBLE);
            }
        });


        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap.put(1, soundPool.load(this, R.raw.excellent, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.great, 2));
        soundPoolMap.put(3, soundPool.load(this, R.raw.good, 3));
        soundPoolMap.put(4, soundPool.load(this, R.raw.perfect, 4));
        activity = this;

        speak_tip_view = findViewById(R.id.speak_tip_view);
        speak_over = (ImageView) findViewById(R.id.speak_over);
        speak_over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //随机播放评分声音
                speak_tip_view.setVisibility(View.INVISIBLE);
                activity.playSound(new Random().nextInt(soundPoolMap.size()) + 1, 0);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        nativeRotationChange(getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        nativeDestory();
    }

    @Override
    public void onResume() {
        super.onResume();
        EasyAR.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        EasyAR.onPause();
    }

    /**
     * 对弹出的cardView中的各个控件值进行相应设置
     *
     * @param dict
     */
    private void setCard(final Dict dict) {
        TextView key = (TextView) cardView.findViewById(R.id.key);
        TextView ps1 = (TextView) cardView.findViewById(R.id.ps1);
        TextView ps2 = (TextView) cardView.findViewById(R.id.ps2);
        TextView pos = (TextView) cardView.findViewById(R.id.pos);
        TextView acceptation = (TextView) cardView.findViewById(R.id.acceptation);
        TextView bilingual = (TextView) cardView.findViewById(R.id.bilingual);
        TextView orig = (TextView) cardView.findViewById(R.id.orig);
        TextView trans = (TextView) cardView.findViewById(R.id.trans);

        key.setTypeface(typeFace);
//        ps1.setTypeface(typeFace);
//        ps2.setTypeface(typeFace);
        pos.setTypeface(typeFace);
        acceptation.setTypeface(typeFace);
        bilingual.setTypeface(typeFace);
        orig.setTypeface(typeFace);
        trans.setTypeface(typeFace);

        ImageView ps1sound = (ImageView) cardView.findViewById(R.id.ps1sound);
        ImageView ps2sound = (ImageView) cardView.findViewById(R.id.ps2sound);
        ImageView close = (ImageView) cardView.findViewById(R.id.close);

        key.setText(dict.getKey());
        ps1.setText("美 [" + dict.getPs_prons().get(0).getPs() + "]");
        ps2.setText("英 [" + dict.getPs_prons().get(1).getPs() + "]");
        pos.setText(dict.getPos_acceptations().get(0).getPos());
        acceptation.setText(dict.getPos_acceptations().get(0).getAcceptation());
        orig.setText(dict.getSents().get(0).getOrig().trim());
        trans.setText(dict.getSents().get(0).getTrans().trim());

        ps1sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放美音
                String path = dict.getPs_prons().get(0).getPron();
                Uri uri = Uri.parse(path);
                player.play(getApplicationContext(), uri, false, AudioManager.STREAM_MUSIC);
            }
        });
        ps2sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放英音
                String path = dict.getPs_prons().get(1).getPron();
                Uri uri = Uri.parse(path);
                player.play(getApplicationContext(), uri, false, AudioManager.STREAM_MUSIC);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.INVISIBLE);
            }
        });
    }

    /**
     * 分享
     */
    private void share(SHARE_MEDIA num) {
        Bitmap shot = PicUtils.takeShot(this);
        PicUtils.share(num, this, shot);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        super.onBackPressed();
    }


    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
        //参数：1、Map中取值   2、当前音量     3、最大音量  4、优先级   5、重播次数   6、播放速度
    }

}
