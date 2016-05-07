package com.left.shothappy.config;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.left.shothappy.utils.AssetCopyer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.socialize.PlatformConfig;

import java.io.IOException;
import java.util.List;

/**
 * Created by left on 16/3/27.
 */
public class MyApplication extends Application {

    private List<String> rewards;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean val = data.getBoolean("status");
            System.out.println("copy textures status:" + val);
        }
    };
    /**
     * 将贴图文件复制到sdcard中
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            AssetCopyer assetCopyer = new AssetCopyer(getApplicationContext());

            boolean iscopyed = false;

            try {
                iscopyed = assetCopyer.copy();
            } catch (IOException e) {
                e.printStackTrace();
            }
            data.putBoolean("status", iscopyed);

            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        PlatformConfig.setWeixin("wx80fa53e66bd0dd39", "7baeecf5dfed24a2e89df2f0df95eb66");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("2761194626", "c710eb199bef6b2b3b694e9b6ded6d88");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("1105313354", "ScEJ6KwGq3WRz3VZ");
        // QQ和Qzone appid appkey

        //开启拷贝
        new Thread(networkTask).start();

    }

    public List<String> getRewards() {
        return rewards;
    }

    public void setRewards(List<String> rewards) {
        this.rewards = rewards;
    }

}
