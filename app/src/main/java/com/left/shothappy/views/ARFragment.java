package com.left.shothappy.views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.left.shothappy.R;
import com.left.shothappy.utils.Renderer;

import cn.easyar.engine.EasyAR;

/**
 * AR认知的页面
 */
public class ARFragment extends Fragment {

    static String key = "d5356f4fba54d722115519ad830267a5zb8JO6yVcvKqdMGZREIYgvtkTjIlmPiUibOw0ge9OsN5DjcVfrOJKpUGM1MwEavrkvcZuEVvKB78wbeIsscymKohIytJAzPWYRhcMlDD3q9oKr5uBTiVtUHizWuMpcxxo0LLtKRdwJE0rbTEnliocezla7mnTJXmN1PzwniC";

    static {
        System.loadLibrary("EasyAR");
        System.loadLibrary("ARNative");
    }

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

}
