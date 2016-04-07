package com.left.shothappy.views;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.left.shothappy.R;
import com.left.shothappy.bean.Dict;
import com.left.shothappy.utils.IcibaTranslate;
import com.left.shothappy.utils.Renderer;
import com.left.shothappy.utils.ScheduleUtils;

import cn.easyar.engine.EasyAR;

/**
 * AR认知的页面
 */
public class ARFragment extends Fragment {

    private static String key = "d5356f4fba54d722115519ad830267a5zb8JO6yVcvKqdMGZREIYgvtkTjIlmPiUibOw0ge9OsN5DjcVfrOJKpUGM1MwEavrkvcZuEVvKB78wbeIsscymKohIytJAzPWYRhcMlDD3q9oKr5uBTiVtUHizWuMpcxxo0LLtKRdwJE0rbTEnliocezla7mnTJXmN1PzwniC";

    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("ARNative");
    }

    private FloatingActionButton fab;
    private CardView cardView;
    private Dict dict;
    private MediaPlayer player;
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
                if (cardView.getVisibility() == View.VISIBLE) {
                    cardView.setVisibility(View.INVISIBLE);
                } else {
                    setCard(dict);
                    cardView.setVisibility(View.VISIBLE);
                    //记得去更新Schedule
                    ScheduleUtils.UpdateSchedule(getActivity(), dict.getKey());

                }

            } else {
                Snackbar.make(getView(), val, Snackbar.LENGTH_LONG).show();
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


            String source = "word";
            try {
                dict = IcibaTranslate.translate(source);
                data.putString("status", "true");//表示请求成功
            } catch (Exception e) {
                e.printStackTrace();
                data.putString("status", "翻译失败，请检查网络");
            }
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    public static native void nativeInitGL();

    public static native void nativeResizeGL(int w, int h);

    public static native void nativeRender();

    private native boolean nativeInit();

    private native void nativeDestory();

    private native void nativeRotationChange(boolean portrait);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ar, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        EasyAR.initialize(getActivity(), key);
        nativeInit();

        GLView glView = new GLView(getContext());
        glView.setRenderer(new Renderer());
        glView.setZOrderMediaOverlay(true);

        ((ViewGroup) view.findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        nativeRotationChange(getActivity().getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);


        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        cardView = (CardView) view.findViewById(R.id.cardview);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 开启一个子线程，进行翻译与语音API请求，注意，不能直接在主线程操作
                new Thread(networkTask).start();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        nativeRotationChange(getActivity().getWindowManager().getDefaultDisplay().getRotation() == android.view.Surface.ROTATION_0);
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

        key.setText(dict.getKey());
        ps1.setText("美 [" + dict.getPs_prons().get(0).getPs() + "]");
        ps2.setText("英 [" + dict.getPs_prons().get(1).getPs() + "]");
        pos.setText(dict.getPos_acceptations().get(0).getPos());
        acceptation.setText(dict.getPos_acceptations().get(0).getAcceptation());
        orig.setText(dict.getSents().get(0).getOrig());
        trans.setText(dict.getSents().get(0).getTrans());

        ps1sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放美音
                String path = dict.getPs_prons().get(0).getPron();
                Uri uri = Uri.parse(path);
                player = MediaPlayer.create(getContext(), uri);
                player.start();
            }
        });
        ps2sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放英音
                String path = dict.getPs_prons().get(1).getPron();
                Uri uri = Uri.parse(path);
                player = MediaPlayer.create(getContext(), uri);
                player.start();
            }
        });
    }

}
