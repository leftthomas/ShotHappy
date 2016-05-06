package com.left.shothappy;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AsyncPlayer;
import android.media.AudioManager;
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
import com.left.shothappy.utils.IcibaTranslate;
import com.left.shothappy.utils.PicUtils;
import com.left.shothappy.utils.Renderer;
import com.left.shothappy.utils.ScheduleUtils;
import com.left.shothappy.views.GLView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import cn.easyar.engine.EasyAR;

/**
 * AR认知的页面
 */
public class ARActivity extends AppCompatActivity {

    private static String key = "d5356f4fba54d722115519ad830267a5zb8JO6yVcvKqdMGZREIYgvtkTjIlmPiUibOw0ge9OsN5DjcVfrOJKpUGM1MwEavrkvcZuEVvKB78wbeIsscymKohIytJAzPWYRhcMlDD3q9oKr5uBTiVtUHizWuMpcxxo0LLtKRdwJE0rbTEnliocezla7mnTJXmN1PzwniC";
    private static View view, share_panel;

    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("ARNative");
    }

    private ImageView fab;
    private CardView cardView;
    private Dict dict;
    private AsyncPlayer player;
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
                                cardView.setVisibility(View.VISIBLE);
                            }
                        }).show();
                //记得去更新Schedule
                ScheduleUtils.UpdateSchedule(ARActivity.this, dict.getKey());
            } else {
                // UI界面的更新等相关操作
                cardView.setVisibility(View.INVISIBLE);
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
    private ImageView share, back, close, share_wechatmoments, share_wechat, share_qzone, share_qq, share_weibo;

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

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
        setTitle(R.string.title_ar);
        EasyAR.initialize(this, key);
        nativeInit();
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
        TextView orig = (TextView) cardView.findViewById(R.id.orig);
        TextView trans = (TextView) cardView.findViewById(R.id.trans);

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

}