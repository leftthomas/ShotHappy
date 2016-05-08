package com.left.shothappy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.View;

import com.left.shothappy.R;
import com.left.shothappy.bean.User;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by left on 16/4/6.
 */
public class PicUtils {

    /**
     * 获取当前activity的截图
     *
     * @param activity
     * @return
     */
    public static Bitmap takeShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return compassImage(bmp);
    }

    /**
     * 分享面板生成
     */
    public static void share(SHARE_MEDIA num, final Activity activity, Bitmap photo) {

        UMImage image = new UMImage(activity, photo);
        new ShareAction(activity).setPlatform(num)
                .withTitle(activity.getString(R.string.app_name))
                .withText(activity.getString(R.string.share_activity_text))
                .withTargetUrl(activity.getString(R.string.URL))
                .withMedia(image)
                .share();
    }

    /**
     * 保存Bitmap为file
     *
     * @param bmp
     * @return
     */
    public static File saveBitmap2file(Bitmap bmp, User user) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        File out = new File(Environment.getExternalStorageDirectory(), "/" + user.getUsername() + "_head.jpg");
        if (!out.exists()) {
            try {
                out.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            stream = new FileOutputStream(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(format, quality, stream);
        return out;
    }

    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public static void cropPhoto(Uri uri, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, 3);
    }

    /**
     * 压缩图片到100KB以下，防止新浪微博分享不出去
     *
     * @return
     */
    public static Bitmap compassImage(Bitmap bitmap) {
        //图片允许最大空间   单位：KB
        double maxSize = 100.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }


    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLocalBitmapByAssets(Context c, String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = c.getResources().getAssets().open(url);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return bitmap;
    }
}
