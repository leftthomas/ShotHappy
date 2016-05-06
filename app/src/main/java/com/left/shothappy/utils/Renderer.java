/**
 * Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
 * EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
 * and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
 */

package com.left.shothappy.utils;

import android.opengl.GLSurfaceView;

import com.left.shothappy.ARActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        ARActivity.nativeInitGL();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        ARActivity.nativeResizeGL(w, h);
    }

    public void onDrawFrame(GL10 gl) {
        ARActivity.nativeRender();
    }

}
