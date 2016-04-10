package com.left.shothappy.config;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by left on 16/3/27.
 */
public class MyApplication extends Application {

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
    }
}
